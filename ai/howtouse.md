# Sleephow

Deep neural network for predicting sleep time and quality for any given bedtime. It uses your personal sleep data exported from Sleep Cycle app to train a model to make predictions. It can give you an estimation of how long you'll sleep, when you will wake up and what your sleep quality is going to be.

## Prerequisites

You will need TensorFlow, `matplotlib` and `python-tk`. To install TensorFlow please refer to their [installation guides](https://www.tensorflow.org/install/). For other dependencies on debian systems

```
sudo apt install python-tk python-pip
pip install matplotlib
```

then run

```
python setup.py
```

which will create directories for saving the model and training plots.

## How to run

You need to execute three (3) steps: build the dataset, train the network and then run `sleephow.py`.

**Building the dataset**

Export your sleepdata from SleepCycle. It's found under Settings -> Advanced -> Export. You will get a `.csv` file that you can feed to `build.py`. Just run it, it will ask for the file:

```
$ python build.py
path to sleepcycle dataset (./sleepdata.csv):
```

**Training the network**

To train the network, simply run `train.py`. It will ask for your dataset file you built in the previous step:

```
$ python train.py
...
epoch 24850 error -> 2.3861, test_acc=73.91%
epoch 24900 error -> 2.3857, test_acc=73.91%
epoch 24950 error -> 2.3854, test_acc=73.91%
done training. peak accuracy was 76.09% @epoch 14150
lowest total error was 2.39 @epoch 24950
(1) example prediction: [[ 0.30978218  0.78051013]] [ 0.28819444  0.63      ]
(2) example prediction: [[ 0.37194026  0.88301277]] [ 0.4375  0.98  ]
model saved to ./model/sleepmodel*
plotting training data...
```

you should get around 75% accuracy with 150 datapoints, splitting training-test data 80-20. You might have to tweak some training parameters like `training_epochs` or the split ratio.

**Predicting sleep**

Once you've trained the model you can run `sleephow.py` to predict your sleep:

```
$ python sleephow.py
```

you'll get a result similar to below

```
At what time are you going to bed? (format=HH:MM) 23:30
Weekday diff from today (default=0)? 1

You will sleep 8 hours 20 minutes, wake up on
	Tuesday, 04/07 at approximately 07:50
		with sleep quality 82.6%
```
