if [ "$(uname)" == "Linux" ]; then
    python src/setup.py install
    pip install -U spacy
    pip install -U spacy-lookups-data
    python -m spacy download pt_core_news_sm
    install -g 0 -o 0 -m 0644 man/verbname.8 /usr/local/man/man8/
    gzip /usr/local/man/man8/verbname.8
elif [ "$(uname)" == "Darwin" ]; then
    python3 src/setup.py install
    pip install -U spacy
    pip install -U spacy-lookups-data
    python3 -m spacy download pt_core_news_sm
    install -g 0 -o 0 -m 0644 man/verbname.8 /usr/share/man/man8/
    gzip /usr/share/man/man8/verbname.8
fi
