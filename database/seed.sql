-- Optional local-development data
-- Apply schema.sql first, then run: mysql -u root -p < database/seed.sql

USE campuscare_helpdesk;

INSERT INTO tickets (
    title,
    description,
    category,
    status,
    student_name,
    student_email
)
SELECT
    'Unable to access campus Wi-Fi',
    'The Wi-Fi login page does not accept my student account.',
    'IT_SUPPORT',
    'OPEN',
    'Asha Rao',
    'asha.rao@example.com'
WHERE NOT EXISTS (
    SELECT 1
    FROM tickets
    WHERE title = 'Unable to access campus Wi-Fi'
      AND student_email = 'asha.rao@example.com'
);

INSERT INTO tickets (
    title,
    description,
    category,
    status,
    student_name,
    student_email
)
SELECT
    'Library card is not active',
    'My newly issued student card is not recognized at the library entrance.',
    'LIBRARY',
    'IN_PROGRESS',
    'Rahul Mehta',
    'rahul.mehta@example.com'
WHERE NOT EXISTS (
    SELECT 1
    FROM tickets
    WHERE title = 'Library card is not active'
      AND student_email = 'rahul.mehta@example.com'
);
