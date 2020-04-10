# Plot sleep predictor training performance
# Copyright (c) Andreas Urbanski, 2017
import matplotlib.pyplot as plt
import numpy as np

def plot_training(step_size, error_history, accuracy_history):
    print 'plotting training data...'

    error_history = np.array(error_history)
    accuracy_history = np.array(accuracy_history)

    with plt.rc_context({
        'axes.edgecolor':'white',
        'axes.facecolor': '151515',
        'axes.labelcolor': 'white',
        'axes.grid': True,
        'grid.color': '222222',
        'xtick.color':'white',
        'ytick.color':'white',
        'text.color': 'white',
        }):
        plt.figure(figsize=(16, 9))
        plt.plot(step_size * np.arange(error_history.size), 100.0 * (error_history / np.max(error_history, axis=0)), linewidth=1.0, color='#ff1c73')
        plt.plot(step_size * np.arange(accuracy_history.size), 100.0 * accuracy_history, linewidth=1.0, color='#2bfffb')
        plt.ylabel('error + test_acc')
        plt.xlabel('epoch')
        plt.savefig('plots/training.png', facecolor='151515', edgecolor='151515')
