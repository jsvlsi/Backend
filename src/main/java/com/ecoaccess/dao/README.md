# DAO package note

`AccountDao.java` and `AccountRepository.java` are legacy combined persistence classes. Do not use them for new code.

Use these classes instead:

- `PassengerDao` for passenger persistence
- `StaffDao` for staff persistence
- `AdminDao` for admin persistence

**TODO: remove `AccountDao.java` and `AccountRepository.java` after all existing service and console references have been migrated and the project compiles successfully.**
