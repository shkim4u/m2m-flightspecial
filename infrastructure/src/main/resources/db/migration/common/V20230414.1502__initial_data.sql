-- >>>>>>>>> FLIGHT SPECIALS <<<<<<<<<
WITH exp_date AS (
  SELECT to_timestamp(extract(epoch from now())) + INTERVAL '79200 seconds' + (random() * 20000000) * INTERVAL '1 microsecond' AS expiry_date
)
INSERT INTO travelbuddy.flightspecial (expiry_date, cost, header, body, origin, origin_code, destination, destination_code)
SELECT expiry_date, 50 + random() * 200, 'London to Prague', 'Jewel of the East', 'London', 'LHR', 'Paris', 'CDG'
FROM exp_date;

WITH exp_date AS (
  SELECT to_timestamp(extract(epoch from now())) + INTERVAL '79200 seconds' + (random() * 20000000) * INTERVAL '1 microsecond' AS expiry_date
)
INSERT INTO travelbuddy.flightspecial (expiry_date, cost, header, body, origin, origin_code, destination, destination_code)
SELECT expiry_date, 50 + random() * 200, 'Paris to London', 'Weekend getaway!', 'Origin', 'ORG', 'Destination', 'DST'
FROM exp_date;

WITH exp_date AS (
  SELECT to_timestamp(extract(epoch from now())) + INTERVAL '79200 seconds' + (random() * 20000000) * INTERVAL '1 microsecond' AS expiry_date
)
INSERT INTO travelbuddy.flightspecial (expiry_date, cost, header, body, origin, origin_code, destination, destination_code)
SELECT expiry_date, 50 + random() * 200, 'Dubai to Cairo', 'Middle East adventure', 'Origin', 'ORG', 'Destination', 'DST'
FROM exp_date;

WITH exp_date AS (
  SELECT to_timestamp(extract(epoch from now())) + INTERVAL '79200 seconds' + (random() * 20000000) * INTERVAL '1 microsecond' AS expiry_date
)
INSERT INTO travelbuddy.flightspecial (expiry_date, cost, header, body, origin, origin_code, destination, destination_code)
SELECT expiry_date, 50 + random() * 200, 'Melbourne to Hawaii', 'Escape to the sun this winter', 'Origin', 'ORG', 'Destination', 'DST'
FROM exp_date;

WITH exp_date AS (
  SELECT to_timestamp(extract(epoch from now())) + INTERVAL '79200 seconds' + (random() * 20000000) * INTERVAL '1 microsecond' AS expiry_date
)
INSERT INTO travelbuddy.flightspecial (expiry_date, cost, header, body, origin, origin_code, destination, destination_code)
SELECT expiry_date, 50 + random() * 200, 'Buenos Aires to Rio', 'Time to carnivale!', 'Origin', 'ORG', 'Destination', 'DST'
FROM exp_date;

WITH exp_date AS (
  SELECT to_timestamp(extract(epoch from now())) + INTERVAL '79200 seconds' + (random() * 20000000) * INTERVAL '1 microsecond' AS expiry_date
)
INSERT INTO travelbuddy.flightspecial (expiry_date, cost, header, body, origin, origin_code, destination, destination_code)
SELECT expiry_date, 50 + random() * 200, 'Sydney to Rome', 'An Italian classic', 'Origin', 'ORG', 'Destination', 'DST'
FROM exp_date;

WITH exp_date AS (
  SELECT to_timestamp(extract(epoch from now())) + INTERVAL '79200 seconds' + (random() * 20000000) * INTERVAL '1 microsecond' AS expiry_date
)
INSERT INTO travelbuddy.flightspecial (expiry_date, cost, header, body, origin, origin_code, destination, destination_code)
SELECT expiry_date, 50 + random() * 200, 'Melbourne to Sydney', 'Well trodden path', 'Origin', 'ORG', 'Destination', 'DST'
FROM exp_date;

WITH exp_date AS (
  SELECT to_timestamp(extract(epoch from now())) + INTERVAL '79200 seconds' + (random() * 20000000) * INTERVAL '1 microsecond' AS expiry_date
)
INSERT INTO travelbuddy.flightspecial (expiry_date, cost, header, body, origin, origin_code, destination, destination_code)
SELECT expiry_date, 50 + random() * 200, 'Hong Kong to Kuala Lumpur', 'Hop step and a jump', 'Origin', 'ORG', 'Destination', 'DST'
FROM exp_date;

WITH exp_date AS (
  SELECT to_timestamp(extract(epoch from now())) + INTERVAL '79200 seconds' + (random() * 20000000) * INTERVAL '1 microsecond' AS expiry_date
)
INSERT INTO travelbuddy.flightspecial (expiry_date, cost, header, body, origin, origin_code, destination, destination_code)
SELECT expiry_date, 50 + random() * 200, 'Lisbon to Madrid', 'Spanish adventure', 'Origin', 'ORG', 'Destination', 'DST'
FROM exp_date;

WITH exp_date AS (
  SELECT to_timestamp(extract(epoch from now())) + INTERVAL '79200 seconds' + (random() * 20000000) * INTERVAL '1 microsecond' AS expiry_date
)
INSERT INTO travelbuddy.flightspecial (expiry_date, cost, header, body, origin, origin_code, destination, destination_code)
SELECT expiry_date, 50 + random() * 200, 'Aswan to Cairo', 'An experience of a lifetime', 'Origin', 'ORG', 'Destination', 'DST'
FROM exp_date;

WITH exp_date AS (
  SELECT to_timestamp(extract(epoch from now())) + INTERVAL '79200 seconds' + (random() * 20000000) * INTERVAL '1 microsecond' AS expiry_date
)
INSERT INTO travelbuddy.flightspecial (expiry_date, cost, header, body, origin, origin_code, destination, destination_code)
SELECT expiry_date, 50 + random() * 200, 'New York to London', 'Trans-Atlantic', 'Origin', 'ORG', 'Destination', 'DST'
FROM exp_date;