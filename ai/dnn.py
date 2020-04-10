# Deep neural network for predicting sleep time and quality
# Copyright (c) Andreas Urbanski, 2017
import tensorflow as tf
import numpy as np

# Build our multilayer network
x = tf.placeholder(tf.float32, shape=[None, 2], name='x')
y_ = tf.placeholder(tf.float32, shape=[None, 2], name='y_')

W1 = tf.Variable(tf.truncated_normal([2, 8], stddev=0.1), name='weight')
b1 = tf.Variable(tf.constant(0.1, shape=[8]), name='bias')

W2 = tf.Variable(tf.truncated_normal([8, 32], stddev=0.1), name='weight')
b2 = tf.Variable(tf.constant(0.1, shape=[32]), name='bias')

W3 = tf.Variable(tf.truncated_normal([32, 64], stddev=0.1), name='weight')
b3 = tf.Variable(tf.constant(0.1, shape=[64]), name='bias')

W4 = tf.Variable(tf.truncated_normal([64, 2], stddev=0.1), name='weight')
b4 = tf.Variable(tf.constant(0.1, shape=[2]), name='bias')

L1 = tf.nn.sigmoid(tf.matmul(x, W1) + b1, name='layer')
L2 = tf.nn.relu6(tf.matmul(L1, W2) + b2, name='layer')
L3 = tf.nn.relu6(tf.matmul(L2, W3) + b3, name='layer')

# Output layer
y = tf.nn.sigmoid(tf.matmul(L3, W4)+ b4, name='y')

# Mean squared error
c = tf.reduce_sum(tf.pow(y - y_, 2))

# Correct prediction when error is +-10%
k = tf.less_equal(tf.abs(y - y_), y_ * .1)

# Accuracy
a = tf.reduce_mean(tf.cast(k, tf.float32))

# Optimization step
o = tf.train.AdamOptimizer(learning_rate=1e-4).minimize(c)
