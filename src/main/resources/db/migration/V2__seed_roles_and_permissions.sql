DO
$$
    DECLARE
        user_role  UUID := gen_random_uuid();
        admin_role UUID := gen_random_uuid();
        p1         UUID := gen_random_uuid();
        p2         UUID := gen_random_uuid();
        p3         UUID := gen_random_uuid();
        p4         UUID := gen_random_uuid();
    BEGIN

        INSERT INTO ROLES (id, name, created_date, created_by)
        VALUES (user_role, 'ROLE_USER', NOW(), '00000000-0000-0000-0000-000000000000'),
               (admin_role, 'ROLE_ADMIN', NOW(), '00000000-0000-0000-0000-000000000000');

        INSERT INTO PERMISSIONS (id, name, created_date, created_by)
        VALUES (p1, 'product:read', NOW(), '00000000-0000-0000-0000-000000000000'),
               (p2, 'order:create', NOW(), '00000000-0000-0000-0000-000000000000'),
               (p3, 'order:read', NOW(), '00000000-0000-0000-0000-000000000000'),
               (p4, 'admin:access', NOW(), '00000000-0000-0000-0000-000000000000');

        INSERT INTO ROLE_PERMISSIONS
        VALUES (user_role, p1),
               (user_role, p2),
               (user_role, p3),
               (admin_role, p1),
               (admin_role, p2),
               (admin_role, p3),
               (admin_role, p4);

    END
$$;