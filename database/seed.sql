-- Optional, non-sensitive demonstration data.
-- Run after schema.sql while connected to the campuscare_helpdesk database.
-- No user credentials are seeded: create users through /api/auth/register and
-- provision the administrator through CAMPUSCARE_ADMIN_* deployment secrets.

SET NAMES utf8mb4 COLLATE utf8mb4_unicode_ci;
START TRANSACTION;

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

COMMIT;
