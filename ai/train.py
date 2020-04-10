# Train sleep predictor from previously built dataset
# Copyright (c) Andreas Urbanski, 2017
from plot import plot_training
from dnn import *
import pickle

# Peaks: accuracy, epoch, lowest error, epoch
peaks = [0, 0, np.inf, 0]

# Error value per epoch every step_size
error_history = []

# Accuracy per epoch every step_size
accuracy_history = []

# total number of training epochs
training_epochs = 25000
step_size = 50

# Seed RNGs
np.random.seed(1)
tf.set_random_seed(1)

# Read input data
data = pickle.load(open(raw_input('path to dataset (sleepdata.pkl): ') or 'sleepdata.pkl', 'r'))

# Randomly shuffle data
np.random.shuffle(data)

# Reshape [[a, b, c, d], ...] -> [[[a, b], ...], [[c, d], ...]]
reshape = lambda a: np.split(a, 2, axis=1)

# Split dataset, 85% training data, 15% test data
training_data, test_data = np.split(data, [int(.85 * data.shape[0])])

# Reshape data
training_data = reshape(training_data)
test_data = reshape(test_data)

# Get a session and graph saver
sess = tf.Session()
saver = tf.train.Saver()

# Initialize
sess.run(tf.global_variables_initializer())

# Train our model
for epoch in xrange(training_epochs):
    _, error = sess.run([o, c], feed_dict={x: training_data[0], y_: training_data[1]})

    if epoch % step_size == 0:
        accuracy = sess.run(a, feed_dict={x: test_data[0], y_: test_data[1]})

        if accuracy > peaks[0]:
            peaks[0] = accuracy
            peaks[1] = epoch

        if error < peaks[2]:
            peaks[2] = error
            peaks[3] = epoch

        error_history.append(error)
        accuracy_history.append(accuracy)

        print 'epoch {0} error -> {1:.4f}, test_acc={2:.2f}%'.format(epoch, error, accuracy * 100.0)

# Training done, show some stats
print 'done training. peak accuracy was {0:.2f}% @epoch {1}'.format(
    peaks[0] * 100.0,
    peaks[1])

print 'lowest total error was {0:.2f} @epoch {1}'.format(
    peaks[2],
    peaks[3])

# Show some example predictions
print '(1) example prediction:', y.eval(session=sess, feed_dict={x: [test_data[0][0]]}), test_data[1][0]
print '(2) example prediction:', y.eval(session=sess, feed_dict={x: [test_data[0][1]]}), test_data[1][1]

# Save model
saver.save(sess, 'model/sleepmodel')
print 'model saved to ./model/sleepmodel*'

# Plot training performance
plot_training(step_size, error_history, accuracy_history)
