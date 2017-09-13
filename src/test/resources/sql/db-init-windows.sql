
psql     -U postgres -p 35432 -c "CREATE ROLE blog_owner LOGIN ENCRYPTED PASSWORD 'blog_owner' NOSUPERUSER;"
createdb -U postgres -p 35432 -O blog_owner blog
psql     -U postgres -p 35432 -c "CREATE ROLE blogdemo_owner LOGIN ENCRYPTED PASSWORD 'blogdemo_owner' NOSUPERUSER;"
createdb -U postgres -p 35432 -O blogdemo_owner blogdemo
