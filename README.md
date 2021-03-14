# Feedbank README
CS261 Group project

Group 8

## Installation

The installation guide is set up for windows, however similar steps would be required for other operating systems.

Install Postgres 12 onto your device with pgadmin for ease of use.

Windows installer: https://www.postgresql.org/download/windows/

Use the package manager [pip](https://pip.pypa.io/en/stable/) to install the following libraries using your terminal.

```bash
pip install nltk
pip install flask
```
Then run script.py by typing this in your terminal
```bash
python script.py
```

Ensure atleast java version 8 is installed.

Start the Postgres server by opening pgAdmin4. 
In the pgAdmin4 go to Servers -> PostgreSQL -> Databases. Right click on Databases and create a new database called *springfeedback* with all other settings at default and save.

Then select springfeedbank, and at the top go into Tools -> Query Tool. 
Copy the database Schema in src\main\resources\static\schema.sql into the pgAdmin4 query editor and run it.

Ensure that PostgresSql -> Properties -> Connection has port number set to 5432.

Start the spring server on local host by running the executable jar feedbank.jar:
 ```bash
java -jar feedbank.jar
```

The URL http://localhost:8080/login will take you to the site login portal. Note that due to the way http sessions work,
to test the program with multiple users, run each user client on a different browser.
