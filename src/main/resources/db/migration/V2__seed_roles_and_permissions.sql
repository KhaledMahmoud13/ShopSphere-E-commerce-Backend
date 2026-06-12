DO
$$
    DECLARE
        user_role  UUID := gen_random_uuid();
        admin_role UUID := gen_random_uuid();
        p1         UUID := gen_random_uuid();
        p2         UUID := gen_random_uuid();
        p3         UUID := gen_random_uuid();
        p4         UUID := gen_random_uuid();
        p5         UUID := gen_random_uuid();
        p6         UUID := gen_random_uuid();
        p7         UUID := gen_random_uuid();
        p8         UUID := gen_random_uuid();
        p9         UUID := gen_random_uuid();
        p10        UUID := gen_random_uuid();
        p11        UUID := gen_random_uuid();
        p12        UUID := gen_random_uuid();
    BEGIN

        INSERT INTO ROLES (id, name, created_date, created_by)
        VALUES (user_role, 'ROLE_USER', NOW(), '00000000-0000-0000-0000-000000000000'),
               (admin_role, 'ROLE_ADMIN', NOW(), '00000000-0000-0000-0000-000000000000');

        INSERT INTO PERMISSIONS (id, name, created_date, created_by)
        VALUES (p1, 'product:read', NOW(), '00000000-0000-0000-0000-000000000000'),
               (p2, 'order:create', NOW(), '00000000-0000-0000-0000-000000000000'),
               (p3, 'order:read', NOW(), '00000000-0000-0000-0000-000000000000'),
               (p4, 'order:update-status', NOW(), '00000000-0000-0000-0000-000000000000'),
               (p5, 'cart:read', NOW(), '00000000-0000-0000-0000-000000000000'),
               (p6, 'cart:write', NOW(), '00000000-0000-0000-0000-000000000000'),
               (p7, 'checkout:create', NOW(), '00000000-0000-0000-0000-000000000000'),
               (p8, 'payment:create', NOW(), '00000000-0000-0000-0000-000000000000'),
               (p9, 'payment:read', NOW(), '00000000-0000-0000-0000-000000000000'),
               (p10, 'address:read', NOW(), '00000000-0000-0000-0000-000000000000'),
               (p11, 'address:write', NOW(), '00000000-0000-0000-0000-000000000000'),
               (p12, 'admin:access', NOW(), '00000000-0000-0000-0000-000000000000');

        INSERT INTO ROLE_PERMISSIONS
        VALUES (user_role, p1),
               (user_role, p2),
               (user_role, p3),
               (user_role, p5),
               (user_role, p6),
               (user_role, p7),
               (user_role, p8),
               (user_role, p9),
               (user_role, p10),
               (user_role, p11),

               (admin_role, p1),
               (admin_role, p2),
               (admin_role, p3),
               (admin_role, p4),
               (admin_role, p5),
               (admin_role, p6),
               (admin_role, p7),
               (admin_role, p8),
               (admin_role, p9),
               (admin_role, p10),
               (admin_role, p11),
               (admin_role, p12);

    END
$$;