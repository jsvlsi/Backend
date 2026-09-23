# Pending DAO cleanup

> **Do not delete these legacy files yet.**
>
> The files below are retained temporarily because parts of the application still reference them:
>
> - `src/main/java/com/ecoaccess/dao/AccountDao.java`
> - `src/main/java/com/ecoaccess/dao/AccountRepository.java`
> - `src/main/java/com/ecoaccess/model/Entities.java`
>
> They are legacy compatibility code and should be removed only after all references have been migrated to the entity-specific DAOs and top-level model classes.
>
> ## Required cleanup later
>
> 1. Replace imports of `com.ecoaccess.model.Entities.*` with explicit top-level model imports.
> 2. Replace `AccountDao` usage with `PassengerDao`, `StaffDao`, and `AdminDao`.
> 3. Replace `AccountRepository` usage with the entity-specific DAOs.
> 4. Compile the project and fix constructor/import/type errors.
> 5. Delete the three legacy files listed above.
>
> Until then, leave these files in place so the project does not lose required code. The `job_role` field should still be removed from the database using `sql/04_remove_job_role.sql`.
