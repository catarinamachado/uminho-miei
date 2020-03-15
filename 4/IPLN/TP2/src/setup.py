from setuptools import setup
# you may need setuptools instead of distutils

setup(
    name = "verbname",
    version = "0.1",

    # basic stuff here
    scripts = [
        'src/verbname'
    ],

    #not sure if this works this way
    install_requires=['tabulate>=0.8.5'],
)