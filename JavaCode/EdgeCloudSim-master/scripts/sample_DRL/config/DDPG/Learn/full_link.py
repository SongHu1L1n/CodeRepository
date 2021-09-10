import tensorflow as tf
import random


random.seed()

x = tf.placeholder(tf.float32)
yTrain = tf.placeholder(tf.float32)

w1 = tf.Variable(tf.random_normal([4, 8], mean=0.5, stddev=0.1), dtype=tf.float32)
b1 = tf.Variable(0, dtype=tf.float32)

xr = tf.reshape(x, [1, 4])#将x转换为一个形态为[1,4]的矩阵并保存在xr中

n1 = tf.nn.tanh(tf.matmul(xr, w1)  + b1)

w2 = tf.Variable(tf.random_normal([8, 2], mean=0.5, stddev=0.1), dtype=tf.float32)
b2 = tf.Variable(0, dtype=tf.float32)

n2 = tf.matmul(n1, w2) + b2

y = tf.nn.softmax(tf.reshape(n2, [2]))

loss = tf.reduce_mean(tf.square(y - yTrain))

optimizer = tf.train.RMSPropOptimizer(0.01)

train = optimizer.minimize(loss)

sess = tf.Session()

sess.run(tf.global_variables_initializer())

lossSum = 0.0

for i in range(5):

    xDataRandom = [int(random.random() * 10), int(random.random() * 10), int(random.random() * 10), int(random.random() * 10)]
    if xDataRandom[2] % 2 == 0:
        yTrainDataRandom = [0, 1]
    else:
        yTrainDataRandom = [1, 0]

    result = sess.run([train, x, yTrain, y, loss], feed_dict={x: xDataRandom, yTrain: yTrainDataRandom})

    lossSum = lossSum + float(result[len(result) - 1])

    print("i: %d, loss: %10.10f, avgLoss: %10.10f" % (i, float(result[len(result) - 1]), lossSum / (i + 1)))

