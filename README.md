# Feedbank README
CS261 Group project

##Installation
Install Postgres 12 onto your device. 

Use the package manager [pip](https://pip.pypa.io/en/stable/) to install the following libraries.

```bash
pip install nltk
pip install flask
```
Then run the following python script.
```python
import nltk
nltk.download('vader_lexicon')
nltk.download('stopwords')
nltk.download('punkt')
```

Ensure atleast java version 8 is installed.

Start the Postgres server by opening pgAdmn4 and run the following query:

```SQL
CREATE DATABASE feedbank;
```

Copy the database Schema in src\main\resources\static\schema.sql into the pgAdmin4 query editor and run it.

Start the spring server on local host by running the follow command:
 ```bash
java jar feedbank.jar
```

The URL http://localhost:8080/login will take you to the site login portal. Note that due to the way http sessions work,
to test the program with multiple users, run each user client on a different browser.