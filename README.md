# SingleTaskManager 使用队列实现有序任务管理
在日常开发中经常能碰到需要实现有序任务依次执行的场景。一个最典型的例子就是使用MediaPlayer播放视频。
一个视频的播放需要经过MediaPlayer的create、prepare、start。同样关闭一个视频需要经过stop、reset、release的过程。这些播放和关闭的过程顺序不能改变。并且在一个类中很容易通过一个MediaPlayer的对象对这些过程进行简单的控制。

# 在列表中播放视频
如果要在一个List中播放视频，并做到只有一个视频可以播放，则可以考虑使用SingleTaskManager
