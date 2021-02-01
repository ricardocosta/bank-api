# Set up Database

_This document details how to locally set up the database so that the Bank application works locally._

## Requirements

* PostgreSQL v13
* `psql` should be available as well.

## Connecting

Connect with your default user to the database.

```
psql postgres
```

Use `\conninfo` to make sure you're connected with the correct user:

```
\conninfo
You are connected to database "postgres" as user "ricardo" via socket in "/tmp" at port "5432".
```

## Creating the Database

Create the required database by running
the [`CREATE DATABASE`](https://www.postgresql.org/docs/13/sql-createdatabase.html) command. This will create a new
database based on the pristine `template0` template.

```
CREATE DATABASE app TEMPLATE template0;
```

Connect to the newly created database, and revoke all grants on the `public` schema for all roles.

```
\connect app
REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON DATABASE app FROM PUBLIC;
```

## Creating Roles & Users

### Super

Create a role that can do everything, but on the new database only, with
the [`CREATE ROLE`](https://www.postgresql.org/docs/13/sql-createrole.html) command. Follow by granting privileges with
the [`GRANT`](https://www.postgresql.org/docs/13/sql-grant.html) command.

```
CREATE ROLE app_su_role;
GRANT CONNECT ON DATABASE app to app_su_role;
GRANT ALL ON SCHEMA public TO app_su_role;
``` 

Create a user for Flyway migrations with the [`CREATE USER`](https://www.postgresql.org/docs/13/sql-createuser.html)
command.

```
CREATE USER app_flyway NOSUPERUSER NOCREATEDB NOCREATEROLE LOGIN ENCRYPTED PASSWORD 'password';
GRANT CONNECT ON DATABASE app TO app_flyway;
GRANT app_su_role TO app_flyway;
```

### Read/Write

Create a role for read/write operations on the database with
the [`CREATE ROLE`](https://www.postgresql.org/docs/13/sql-createrole.html) command.

```
SET ROLE <master_user>;
CREATE ROLE app_rw_role;
GRANT CONNECT ON DATABASE app to app_rw_role;
GRANT USAGE ON SCHEMA public TO app_rw_role;

SET ROLE app_su_role;
GRANT SELECT, USAGE, UPDATE ON ALL SEQUENCES IN SCHEMA public TO app_rw_role;
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO app_rw_role;
GRANT EXECUTE ON ALL FUNCTIONS IN SCHEMA public TO app_rw_role;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO app_rw_role;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT SELECT, USAGE, UPDATE ON SEQUENCES TO app_rw_role;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT EXECUTE ON FUNCTIONS TO app_rw_role;

SET ROLE app_flyway;
GRANT SELECT, USAGE, UPDATE ON ALL SEQUENCES IN SCHEMA public TO app_rw_role;
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO app_rw_role;
GRANT EXECUTE ON ALL FUNCTIONS IN SCHEMA public TO app_rw_role;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO app_rw_role;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT SELECT, USAGE, UPDATE ON SEQUENCES TO app_rw_role;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT EXECUTE ON FUNCTIONS TO app_rw_role;
``` 

Create user for App API with the [`CREATE USER`](https://www.postgresql.org/docs/13/sql-createuser.html) command.

```
SET ROLE <master_user>;
CREATE USER app_api NOSUPERUSER NOCREATEDB NOCREATEROLE LOGIN ENCRYPTED PASSWORD 'password';
GRANT app_rw_role TO app_api;
GRANT CONNECT ON DATABASE app TO app_api;
```

### Read-only

Create a role for read-only operations on the database with
the [`CREATE ROLE`](https://www.postgresql.org/docs/13/sql-createrole.html) command.

```
SET ROLE <master_user>;
CREATE ROLE app_ro_role;
GRANT CONNECT ON DATABASE app to app_ro_role;
GRANT USAGE ON SCHEMA public TO app_ro_role;

SET ROLE app_su_role;
GRANT SELECT ON ALL SEQUENCES IN SCHEMA public TO app_ro_role;
GRANT SELECT ON ALL TABLES IN SCHEMA public TO app_ro_role;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT SELECT ON TABLES TO app_ro_role;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT SELECT ON SEQUENCES TO app_ro_role;

SET ROLE app_flyway;
GRANT SELECT ON ALL SEQUENCES IN SCHEMA public TO app_ro_role;
GRANT SELECT ON ALL TABLES IN SCHEMA public TO app_ro_role;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT SELECT ON TABLES TO app_ro_role;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT SELECT ON SEQUENCES TO app_ro_role;
```

## Testing the Setup

To make sure everything is correctly configured, follow these steps and validate that you obtain the expected result:

### RW user cannot create tables

1. Connect with `app_api` user:

```
$ psql -U app_api app
```

2. Try to create a table:

```
$ CREATE TABLE A(id INT NOT NULL);
```

🔴 Validate that it's not possible:

```
ERROR:  permission denied for schema public
LINE 1: CREATE TABLE A(id INT NOT NULL);
```

### SU user can create tables

1. Connect with `app_flyway` user:

```
$ psql -U app_flyway app
```

2. Try to create a table:

```
$ CREATE TABLE A(id INT NOT NULL);
```

✅ Validate that it is possible:

```
CREATE TABLE
```

### RW user can operate over existing tables

1. Connect with `app_api` user:

```
$ psql -U app_api app
```

2. Try to insert a row in the table:

```
$ INSERT INTO a(id) VALUES(1);
```

✅ Validate that it is possible:

```
INSERT 0 1
```

3. Try to update a row in the table:

```
$ UPDATE a SET id = 2 WHERE id = 1;
```

✅ Validate that it is possible:

```
UPDATE
```

4. Try to view information on the table:

```
$ SELECT * FROM a;
```

✅ Validate that it is possible:

```
 id
----
  2
(1 row)
```

5. Try to delete a row on the table:

```
$ DELETE FROM a WHERE id = 2;
```

✅ Validate that it is possible:

```
DELETE 1
```

6. Try to truncate a table:

```
$ TRUNCATE a;
```

🔴 Validate that it's not possible:

```
ERROR:  permission denied for table a
```

7. Try to delete a table:

```
$ DROP TABLE a;
```

🔴 Validate that it's not possible:

```
ERROR:  must be owner of table a
```

### Cleanup

1. Connect with `app_flyway` user:

```
$ psql -U app_flyway app
```

2. Delete the table:

```
$ DROP TABLE a;
```

✅ Validate that it is gone:

```
DROP TABLE
```

✅ Validate no tables remain:

```
$ \d
Did not find any relations.
```
