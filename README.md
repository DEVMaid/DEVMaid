# DEVMaid 

[![Build Status](https://travis-ci.org/DEVMaid/DEVMaid.svg?branch=master)](https://travis-ci.org/DEVMaid/DEVMaid.svg)

### This software is to increase the efficiency and productivity in all software developments by unifying local and remote servers all in once concepts.

#### Why and What?
Have you ever feel tired of only changing one line of the source file and deploying it to all remote servers manually – such as copying the source and ssh into the remote host and vi that file and paste it and save it, or switching to the deploy terminals to run the build?  These operations usually involves at least 5-10 keystrokes but in the long run this does not increase the productivity at all but fatigue and frustrations.  With this software, let say whenever you are done with editing the source/config files on your local work machine, you can set it up to:
- run a series of custom pre-defined commands such as packaging and deploying
- have all those files get synchronized (i.e. updated) to the remote host(s) AUTOMATICALLY.  Yes, i know every human-being likes the word ‘automatically’!!! :)  

###### How is it compared to rsync when it comes to file synchronizations?

Overall, <a href="http://linux.die.net/man/1/rsync" target="_blank">Rsync</a> is more for system administrators while DEVMaid is more developers-friendly since all configurations are done in the single json file in which i assume every developer who knows java will be already familiar with.  Additionally, since it is not just a linux tool like rsync but a full-fledged JVM based application, DEVMaid provides some cool features in which Rsync does not, but not limited to:


- A time interval can be specified to perform each synchronization.  This can pervent network overhead for big files.
- It can generate a report to summarize what file(s) being synched. (to be implemented soon)
- It can allow to execute customized commands once the synchronization is performed. Very powerful!.  (to be implemented soon)
- ...etc

#### Prerequisites - first time thing

1) This software has been fully developed and built and run on Mac OS X and Linux Ubuntu 14.10.  It should work in future versions.

2) You still need to have Java Development Kit (JDK) installed. Application should compile on JDK version 1.7+, that can be found [here](http://www.oracle.com/technetwork/java/javase/downloads/index.html).

3) Create the config.json in the /etc/DEVMaid by first creating the directory and giving the right permissions:

```bash
sudo mkdir -p /etc/DEVMaid
sudo chmod 774 /etc/DEVMaid
```

Here is a <a href="https://github.com/wwken/DEVMaid/blob/master/src/main/resources/config.json" target="_blank">template</a> you can use.  For example, to connect to localhost with 'ken' as user name and the key file, an example can be <a href="https://github.com/wwken/DEVMaid/blob/master/src/test/resources/test-config-localhost.json" target="_blank">found</a> here. 

4) [Optional!!!] To build and/or run the tests locally, you need 
  - <a href="http://www.scala-sbt.org/" target="_blank">SBT</a> - You can download it from [http://www.scala-sbt.org/download.html](http://www.scala-sbt.org/download.html) website, or you can just find sbt in your favourite package manager (`apt-get`, `yum` or other).
  - <a href="http://hortonworks.com/kb/generating-ssh-keys-for-passwordless-login/" target="_blank">SSH passwordless login to localhost</a> - You need to ssh into your localhost without a password (For Mac user, it can be <a href="http://osxdaily.com/2011/09/30/remote-login-ssh-server-mac-os-x/" target="_blank">done here alternataively</a>).  To set this up, run the following commands: <br/>
```bash
ssh-keygen -t rsa -P '' -f ~/.ssh/id_rsa 
cat ~/.ssh/id_rsa.pub >> ~/.ssh/authorized_keys 
```
  - If you did the above SSH steps, make sure you can ssh into localhost sucessfully by doing:
```bash
ssh localhost
```

##### Runing

-> After the above step, you can now run the DEVMaid by issuing the following command on the project root directory:
```bash
./run.sh --config /etc/DEVMaid/config.json
```
Let say, you have this <a href="https://github.com/wwken/DEVMaid/blob/master/src/test/resources/test-config-iq-spark.json" target="_blank">config.json</a> set up in `/etc/DEVMaid`, that means whenever you are editing the files in `/Users/ken/workspace/DEVMaid/` and `/Users/ken/Documents/workspaceOpenSource-Hadoop/` at your source machine, only files with extensions "*.scala" or "*.java" or "*.py" or "*.txt" will get synchronized to all remote hosts at 'iq-spark-001', 'iq-spark-002' and 'iq-spark-003' at the folder `/home/ubuntu/workspace/` for every 2 seconds.  

Now if you open a web browser, you will be able to see the web interface as well (the default location is: http://localhost:8080 ).  This interface helps us look at all the source/destination files from all machines in one single location!

![Alt text](demo/screen-1.png?raw=true "Web interface for managing the source and destination machines")

-> or Alternatively, you can download the <a href="https://github.com/wwken/DEVMaid/blob/master/release/latest/" target="_blank">latest executable jar</a> (assuming you save it in the current directory) and run the software with these commands:

```bash
jar xf ./DEVMaid*.jar webapp
mkdir -p /etc/DEVMaid/webapp  2>/dev/null -ls
mv webapp /etc/DEVMaid/
java -jar ./DEVMaid*.jar --config /etc/DEVMaid/config.json
```


##### Optional - Building and/or Testing

You can build your own copy of the jar library by issuing the following command on the project root directory:
```bash
./build.sh
```
Or you can build it without building the dependency jar - the web component
```bash
./build.sh --noweb true
```
Or you can build it without running the unit tests
```bash
./build.sh --notest true
```


To run all test cases, do this:
```bash
./run-test.sh
```

To run a particular test, there is a way too!  I have specified any particular tests located in the <a href="https://github.com/wwken/DEVMaid/blob/master/tests/" target="_blank">tests</a> folder, to run any specific test case, for example, UtilTest-translateUserHomeDirIfThereIsOne, do
```bash
./run-test.sh -t UtilTest-translateUserHomeDirIfThereIsOne
```

The prerequisites are to setup the local ssh server and be able to <a href="https://developer.apple.com/library/mac/documentation/Darwin/Reference/ManPages/man1/ssh.1.html" target="_blank">ssh into local host</a> and then specify the hostname, username and keyfile in the test config file at `src/test/resources/test-config-localhost.json` after it is 'git cloned'

###### Related Articles/Misc 

For more details and use case, check it out here at <a href="https://wwken.wordpress.com/2015/10/23/serversynchronizer-the-software-that-synchronizes-all-the-files-from-a-local-server-to-all-remote-servers/" target="_blank">my blog</a>.

For the historical changes logs, it is available in <a href="https://github.com/wwken/DEVMaid/blob/master/CHANGES.md" target="_blank">here</a>.



