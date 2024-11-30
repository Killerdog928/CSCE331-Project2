CREATE TABLE "store_orders" (
  "id" integer PRIMARY KEY,
  "item_name" varchar,
  "price" float,
  "exp_date" date,
  "order_date" date,
  "quantity" integer
);

CREATE TABLE "inventory" (
  "id" integer PRIMARY KEY,
  "name" varchar,
  "amt" integer,
  "restock_amt" integer
);

CREATE TABLE "inventory_history" (
  "id" integer PRIMARY KEY,
  "name" varchar,
  "amt_decremented" integer,
  "timestamp" timestamp
);

CREATE TABLE "sellable" (
  "id" integer PRIMARY KEY,
  "name" varchar,
  "price" float,
  "is_active" bool
);

CREATE TABLE "item" (
  "id" integer PRIMARY KEY,
  "name" varchar,
  "add_price" float
);

CREATE TABLE "sale" (
  "id" integer PRIMARY KEY,
  "employee_id" integer,
  "total_price" float,
  "order_date" timestamp
);

CREATE TABLE "employee" (
  "id" int PRIMARY KEY,
  "name" varchar,
  "start_day" date,
  "job_position" varchar,
  "access_level" int,
  "manager_id" int
);

CREATE TABLE "sold_sellable" (
  "id" integer PRIMARY KEY,
  "sale_id" integer,
  "sellable_id" integer
);

CREATE TABLE "sold_item" (
  "id" integer PRIMARY KEY,
  "item_id" integer,
  "sold_sellable_id" integer,
  "amount" float
);

ALTER TABLE "sale" ADD FOREIGN KEY ("employee_id") REFERENCES "employee" ("id") ON DELETE SET NULL;

ALTER TABLE "employee" ADD FOREIGN KEY ("manager_id") REFERENCES "employee" ("id");

ALTER TABLE "sold_sellable" ADD FOREIGN KEY ("sale_id") REFERENCES "sale" ("id") ON DELETE CASCADE;

ALTER TABLE "sold_sellable" ADD FOREIGN KEY ("sellable_id") REFERENCES "sellable" ("id") ON DELETE CASCADE;

ALTER TABLE "sold_item" ADD FOREIGN KEY ("item_id") REFERENCES "item" ("id") ON DELETE CASCADE;

ALTER TABLE "sold_item" ADD FOREIGN KEY ("sold_sellable_id") REFERENCES "sold_sellable" ("id") ON DELETE CASCADE;
