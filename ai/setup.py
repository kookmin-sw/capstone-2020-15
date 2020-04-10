import os

for dir in ['model', 'plots']:
    try:
        os.makedirs(dir)
        print 'created directory "./{0}"'.format(dir)
    except:
        print 'directory "./{0}" already exists'.format(dir)

print 'done.'
