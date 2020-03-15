
if [ "$(uname)" == "Darwin" ]; then
    rm /Library/Frameworks/Python.framework/Versions/3.7/lib/python3.7/site-packages/verbname-0.1-py3.7.egg
    rm /usr/share/man/man8/verbname.8.gz
    rm -rd /Library/Frameworks/Python.framework/Versions/3.7/bin/verbname
elif [ "$(expr substr $(uname -s) 1 5)" == "Linux" ]; then
    rm /usr/lib/python3.7/site-packages/verbname-0.1-py3.7.egg
    rm /usr/local/man/man8/verbname.8.gz
    rm -rd /usr/bin/verbname
fi
rm -rd verbname.egg-info
rm -rd build
rm -rd dist
