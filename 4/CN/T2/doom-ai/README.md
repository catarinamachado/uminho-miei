# Doom-AI
Deep Reinforcement Learning applied to the game DOOM, using the game environment *VizDoom*.

## Dependencies
* Python 3.6
* VizDoom
* Tensorflow 2.x
* OpenCV
* Numpy

## How to run
```
python3 doom-ai.py
```
To see all the available parameters use `--help`.

To get real-time tracking of the training just access the logs produced by Tensorboard itself:
```
tensorboard --logdir logs/dqn/folder_with_current_time_name
```
