# High-level function for predicting sleep time and quality
# using previously trained DNN
# Copyright (c) Andreas Urbanski, 2017
import tensorflow as tf
from datetime import datetime, timedelta
from build import *

# Predict sleep time and sleep quality give datetime object
def predict_sleep_time_and_quality(date):

    # Get a session
    sess = tf.Session()

    # Import meta graph and restore model
    saver = tf.train.import_meta_graph('model/sleepmodel.meta')
    saver.restore(sess, 'model/sleepmodel')

    # Get default graph
    graph = tf.get_default_graph()

    # Input tensor
    x = graph.get_tensor_by_name('x:0')

    # Feed input to model and get a prediction
    p = sess.run('y:0', feed_dict={x: [[
        normalize(datetime_to_minutes(date), 1440),
        normalize(weekdayn_from_date(date), 6.0)]
    ]})

    # Return sleep time in minutes, sleep quality
    return (
        denormalize(p[0][0], 1440),
        p[0][1]
    )

if __name__ == '__main__':
    print '\n\n'
    print 'predict (t=now)', predict_sleep_time_and_quality(datetime.today())
