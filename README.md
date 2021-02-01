<h1>Bank API</h1>

![ricardocosta](https://circleci.com/gh/ricardocosta/bank-api.svg?style=shield)
![codecov](https://codecov.io/gh/ricardocosta/bank-api/branch/main/graph/badge.svg?token=ON5465MRXB)

API for Bank app.

## 🔌 Running

### Setup

In order to run this application locally, you need to have an instance of PostgreSQL running on your machine. After
that, follow the [Setup Database](./docs/setup-database.md) guide to have everything ready to run.

### Terminal

You can run the application locally in your terminal application with:

```
$ ./gradlew bootRun
```

The application will be available at http://localhost:8080.

### IntelliJ

To run in IntelliJ, just run the `main` method in the `BankAPIApplication` class.

Be reminded that you may have to modify the Run Configuration in order to add environment variables that are not defined
in the `application.yml` file.

The application will be available at http://localhost:8080. The API documentation following OpenAPI Specification v3 is
available at http://localhost:8080/api.html.

## 🧰 Development

Please read the [contributing guide](./CONTRIBUTING.md) for further details on how to develop on this app.

## 🚀 Publishing

To publish a new version of the project:

1. Create a branch for the release:

```
$ git checkout -b release_$(date -u +"%Y%m%d")
```

2. Generate the `CHANGELOG` file automatically based on the commits:

```
$ ./gradlew changelog
```

3. Commit the updated changelog.

```
$ git commit -am "chore(release): v<new_version>"
```

4. Open a Pull Request against `main` and have it merged.

5. Update your local `main` branch, generate the new tag and push it:

```
$ git checkout main
$ git pull
$ git tag v<new_version>
$ git push --tags
```
