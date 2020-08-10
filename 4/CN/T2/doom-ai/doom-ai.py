import tensorflow as tf
from tensorflow.keras.models import Sequential
from tensorflow.keras.layers import Dense, Conv2D, MaxPooling2D, Flatten, Dropout, BatchNormalization
from tensorflow.keras.optimizers import Adam
from tensorflow.keras.initializers import TruncatedNormal
import tensorflow.keras as K
import numpy as np

from collections import deque
import itertools as it
import cv2
import vizdoom
import random
import time
import datetime

def prepare_gpu():
    print("Initializing GPUs...")
    gpus = tf.config.experimental.list_physical_devices('GPU')
    if gpus:
        try:
            for gpu in gpus:
                tf.config.experimental.set_memory_growth(gpu, True)

        except RuntimeError as e:
            print(e)
    print("Finsihed starting GPUs")
def clean_gpu():
    from numba import cuda
    cuda.select_device(0)
    cuda.close()

############################################################ AUX Functions ################################################################################

def preprocess(img):
    #img = np.reshape(img,(img.shape[1],img.shape[2],img.shape[0]))
    img = cv2.resize(img, (resolution[1],resolution[0]))
    img = img.astype(np.float32)
    img = img / 255
    return img

def create_model(n_actions):
    model = Sequential()
    model.add(Conv2D(32,kernel_size=(8,8),strides=(2, 2),input_shape=(4, resolution[0], resolution[1]), data_format="channels_first", activation='elu', padding='valid', kernel_initializer=tf.keras.initializers.GlorotNormal()))
    model.add(BatchNormalization(axis=1, epsilon=1e-5))
    model.add(Conv2D(64,kernel_size=(4,4), strides=(2, 2), activation='elu', padding='valid', kernel_initializer=tf.keras.initializers.GlorotNormal()))
    model.add(BatchNormalization(axis=1, epsilon=1e-5))
    model.add(Conv2D(128,kernel_size=(4,4), strides=(1, 1), activation='elu', padding='valid', kernel_initializer=tf.keras.initializers.GlorotNormal()))
    model.add(BatchNormalization(axis=1, epsilon=1e-5))
    model.add(Flatten())
    model.add(Dense(512, activation='elu', kernel_initializer=tf.keras.initializers.GlorotNormal()))
    model.add(Dense(n_actions, kernel_initializer=tf.keras.initializers.GlorotNormal()))

    opt = Adam(lr=0.001)
    model.compile(opt,'mean_squared_error',['accuracy'])

    return model

def create_model_base(n_actions):
    model = Sequential()
    model.add(Conv2D(8,6,input_shape=(4, resolution[0], resolution[1]), activation='relu', padding='same', kernel_initializer=TruncatedNormal(mean=0.0, stddev=0.05, seed=None)))
    model.add(Conv2D(8,6, activation='relu', padding='same', kernel_initializer=TruncatedNormal(mean=0.0, stddev=0.05, seed=None)))
    model.add(Flatten())
    model.add(Dense(128, activation='relu', kernel_initializer=TruncatedNormal(mean=0.0, stddev=0.05, seed=None)))
    model.add(Dense(n_actions, kernel_initializer=TruncatedNormal(mean=0.0, stddev=0.05, seed=None)))

    opt = Adam(lr=0.001)
    model.compile(opt,'mean_squared_error',['accuracy'])

    return model    


############################################################ DQN Agent ################################################################################


class DQNAgent:
    def __init__(self, n_actions, max_replay_memory, min_replay_memory, minibatch_size, actions, update_target_every, use_latest=False):
        print("Initializing agent...")

        # Double Q-Learning algorithm
        if MODEL_NAME == 'BotKiller':
            self.model = create_model_base(n_actions)
            self.target_model = create_model_base(n_actions)
        elif MODEL_NAME == 'model123':
            self.model = create_model(n_actions)
            self.target_model = create_model(n_actions)
        else:
            self.model = create_model_base(n_actions)
            self.target_model = create_model_base(n_actions)
            
        self.replay_memory = deque(maxlen=max_replay_memory)
        self.min_replay_memory = min_replay_memory
        self.minibatch_size = minibatch_size
        self.actions = actions
        if use_latest:
            self.load_model()
        self.target_model.set_weights(self.model.get_weights())

        self.update_target_every = update_target_every
        self.update_target_counter = 0

        print("Finished the agent!")


    def save_model(self):
        self.model.save(MODEL_NAME)

    def load_model(self):
        self.model = K.models.load_model(MODEL_NAME)

    # (observation space, action, reward, new observation space, done)
    def update_replay_memory(self, transition):
        self.replay_memory.append(transition)

    # Queries main network for Q values given current observation space (environment state)
    def get_qs(self, states):
        states_exp = np.expand_dims(states, axis=0)
        prediction = self.model.predict(states_exp)
        return prediction[0]


    # Trains network every step during episode
    def train(self, isterminal):

        # Start training only if certain number of samples is already saved
        if len(self.replay_memory) < self.min_replay_memory:
            return None

        minibatch = random.sample(self.replay_memory, self.minibatch_size)

        s1_batch = np.array([d[0] for d in minibatch])
        a_batch = [d[1] for d in minibatch]
        r_batch = [d[2] for d in minibatch]
        s2_batch = np.array([d[3] for d in minibatch])

        Y = []
        s2_qs = self.target_model.predict(s2_batch)

        for i in range(0,self.minibatch_size):
            value = 0
            # Check if terminal
            if minibatch[i][4]:
                value = r_batch[i]
            else:
                value = r_batch[i] + GAMMA * np.max(s2_qs[i])
            tmp = self.get_qs(minibatch[i][0])
            tmp[minibatch[i][1]] = value
            Y.append(tmp)

        history = self.model.fit(s1_batch, np.array(Y), batch_size=self.minibatch_size, epochs=1, verbose=0, shuffle=False)

        if isterminal:
            self.update_target_counter += 1
        if self.update_target_counter > self.update_target_every:
            self.update_target_counter = 0
            self.target_model.set_weights(self.model.get_weights())
            print("========> Updated Target <========")

        return history


############################################################ Environment ################################################################################


class Environment:
    def __init__(self, gamma, epsilon, epsilon_disc, max_replay_memory, min_replay_memory, minibatch_size, update_target_every, use_trained=False):
        self.game, self.actions = self.configure_game_training()
        self.agent   = DQNAgent(len(self.actions), max_replay_memory, min_replay_memory, minibatch_size, self.actions, update_target_every, use_latest=use_trained)
        self.gamma = gamma
        self.epsilon_base = epsilon
        self.epsilon_disc = epsilon_disc
        self.epsilon_min = 0.1


    def configure_game_training(self):
        print("Initializing game environment...")
        game = vizdoom.DoomGame()
        game.load_config("basic.cfg")
        game.set_window_visible(False)
        game.set_screen_format(vizdoom.ScreenFormat.GRAY8)

        left        = [1, 0, 0]
        right       = [0, 1, 0] 
        shoot       = [0, 0, 1]
        possible_actions = [left, right, shoot]

        print("Finished game environment!")
        return game, possible_actions

    def train_agent(self, epochs, max_steps):
        current_time = datetime.datetime.now().strftime("%Y%m%d-%H%M%S")
        log_dir = 'logs/dqn/' + MODEL_NAME + "_" + str(epochs) + "_" + current_time
        summary_writer = tf.summary.create_file_writer(log_dir)


        scores = []
        print("---------------Starting training Doom Ai--------------")
        self.game.init()
        step_decay = 0

        for epoch in range(epochs):
            losses = []
            print("-> Episode ",epoch)
            self.game.new_episode()

            # First state to predict on as a starting point
            s1 = preprocess(self.game.get_state().screen_buffer)
            s_t_deque = deque(maxlen=4)
            s_t_deque.append(s1)
            s_t_deque.append(s1)
            s_t_deque.append(s1)
            s_t_deque.append(s1)

            s_t = np.stack(s_t_deque, axis=0)
            step = 0
            while (not self.game.is_episode_finished()) and step < max_steps:
                q_t = self.agent.get_qs(s_t)

                expl_probs = self.epsilon_min + (self.epsilon_base - self.epsilon_min) * np.exp(-self.epsilon_disc * step_decay)

                # Decide if greedy or not
                if random.random() < expl_probs:
                    a = random.randint(0, len(self.actions) - 1)
                else:
                    a = np.argmax(q_t)

                # Execute action
                reward = self.game.make_action(self.actions[a],12)
                isterminal = self.game.is_episode_finished()
                s2 = preprocess(self.game.get_state().screen_buffer) if not isterminal else np.zeros((resolution))
                s_t_deque.append(s2)
                s_t2 = np.stack(s_t_deque, axis=0)

                self.agent.update_replay_memory([s_t, a, reward, s_t2, isterminal])

                # Update current input of states
                s_t = s_t2
                history = self.agent.train(isterminal)
                if history is not None:
                    losses.append(history.history['loss'][0])
                else:
                    losses.append(0)
                step += 1
                step_decay += 1

            if self.game.is_episode_finished():
                final_reward = self.game.get_total_reward()
                print("Final Reward: ", final_reward)
                with summary_writer.as_default():
                    tf.summary.scalar('episode reward', final_reward, step=epoch)
                    tf.summary.scalar('average loss', np.array(losses).mean(), step=epoch)
                    tf.summary.scalar('episode epsilon', expl_probs, step=epoch)
                    tf.summary.scalar('episode steps', step, step=epoch)

                scores.append(final_reward)
                losses = []

            if (epoch % 5) == 0:
                self.agent.save_model()

        train_scores = np.array(scores)
        return train_scores

    def play_agent(self, rounds=10):
        # Reinitialize the game with window visible
        self.game, self.actions = self.configure_game_training()
        self.game.set_window_visible(True)
        self.game.set_mode(vizdoom.Mode.ASYNC_PLAYER)
        self.game.init()
        scores = []

        for r in range(rounds):
            self.game.new_episode()

            s1 = preprocess(self.game.get_state().screen_buffer)
            s_t_deque = deque(maxlen=4)
            s_t_deque.append(s1)
            s_t_deque.append(s1)
            s_t_deque.append(s1)
            s_t_deque.append(s1)

            s_t = np.stack(s_t_deque, axis=0)
            while not self.game.is_episode_finished():
                q_t = self.agent.get_qs(s_t)
                a = np.argmax(q_t)

                self.game.set_action(self.actions[a])
                for _ in range(12):
                    self.game.advance_action()

                s2 = []
                if not self.game.is_episode_finished():
                    s2 = preprocess(self.game.get_state().screen_buffer)
                else:
                    break
                s_t_deque.append(s2)
                s_t = np.stack(s_t_deque, axis=0)

            # Sleep between episodes
            time.sleep(1.0)
            score = self.game.get_total_reward()
            print("Round ", r, ": ", score)
            scores.append(score)
        print("----- Results -----")
        scores = np.array(scores)
        print("Mean Rewards: ", np.mean(scores))
        wins = [1 if i > 0 else 0 for i in scores]
        print("Wins: ", np.sum(wins), "/", rounds)



################################################################################################################################################
################################################################################################################################################
################################################################################################################################################
import os, pathlib, argparse

parser = argparse.ArgumentParser(description='Deep Q-Learning to create the Ultimate DOOM Bot')
parser.add_argument('--mode', default='player', type=str, help='Mode of the program: player or trainer.')
parser.add_argument('--epochs', default=1000, type=int, help='Number of episodes.')
parser.add_argument('--max_steps', default=100, type=int, help='Max steps per epoch.')
parser.add_argument('--min_memory', default=1000, type=int, help='Memory Replay minimum size.')
parser.add_argument('--max_memory', default=50000, type=int, help='Memory Replay maximum size.')
parser.add_argument('--batch_size', default=64, type=int, help='Size of the batch to train with.')
parser.add_argument('--update_every', default=5, type=int, help='Update target network every N epochs.')
parser.add_argument('--gamma', default=0.95, type=float, help='Rate of discount on future q-value.')
parser.add_argument('--epsilon', default=1.0, type=float, help='Rate of exploration.')
parser.add_argument('--decay_rate', default=0.00005, type=float, help='Discount for the epsilon.')
parser.add_argument('--im_width', default=84, type=int, help='Image width to be used on training.')
parser.add_argument('--im_height', default=84, type=int, help='Image height to be used on training.')
parser.add_argument('--bot_name', default='BotKiller', type=str, help='Name for the model to be saved.')
parser.add_argument('--use_latest', default='True', type=str, help='Use checkpoint of the model as a start.')


args = parser.parse_args()

EPOCHS = args.epochs
MAX_STEPS = args.max_steps

MIN_REPLAY_MEMORY   = args.min_memory
MAX_REPLAY_MEMORY   = args.max_memory
MINI_BATCH_SIZE     = args.batch_size
UPDATE_TARGET_EVERY = args.update_every

GAMMA = args.gamma
EPSILON = args.epsilon
EPSILON_DISCOUNT = args.decay_rate

resolution = (args.im_width,args.im_height)

MODEL_NAME = args.bot_name

prepare_gpu()
if args.use_latest == 'True':
    if os.path.isdir(MODEL_NAME):
        env = Environment(GAMMA, EPSILON, EPSILON_DISCOUNT, MAX_REPLAY_MEMORY, MIN_REPLAY_MEMORY, MINI_BATCH_SIZE, UPDATE_TARGET_EVERY, use_trained=True)
    else:
        print("No available checkpoint to start from. Training from scratch...")
        if args.mode == 'player':
            print("Exiting...")
            exit()
        env = Environment(GAMMA, EPSILON, EPSILON_DISCOUNT, MAX_REPLAY_MEMORY, MIN_REPLAY_MEMORY, MINI_BATCH_SIZE, UPDATE_TARGET_EVERY, use_trained=False)
else:
    env = Environment(GAMMA, EPSILON, EPSILON_DISCOUNT, MAX_REPLAY_MEMORY, MIN_REPLAY_MEMORY, MINI_BATCH_SIZE, UPDATE_TARGET_EVERY, use_trained=False)

if args.mode == 'trainer':
    # Train agent
    train_scores = env.train_agent(EPOCHS, MAX_STEPS)   
    print("Results: mean: %.1f±%.1f," % (train_scores.mean(), train_scores.std()),
            "min: %.1f," % train_scores.min(), "max: %.1f," % train_scores.max())
    clean_gpu()
else:
    # Play with agent
    env.play_agent()    
