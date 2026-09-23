# Model-layer refactor

The branch `refactor/clean-architecture-database` introduces one JavaBean class per domain entity under `src/main/java/com/ecoaccess/model`.

Each model has:
- camelCase fields;
- a no-argument constructor for framework/serialization compatibility;
- a full constructor; and
- conventional `getX`/`setX` methods.

The former nested-record container is retained as a deprecated empty compatibility marker while DAO/service migration is performed incrementally. Existing DAO/service code still imports the old nested types, so this branch intentionally separates the model API first without changing business behavior. The next migration step is to update imports and record accessor calls (`value()` to `getValue()`) across DAO, service, console, and tests.
