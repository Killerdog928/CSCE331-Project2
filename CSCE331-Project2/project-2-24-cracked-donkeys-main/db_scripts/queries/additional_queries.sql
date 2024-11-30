-- Query 1: Number of orders per employee
SELECT 
    e.name AS employee_name, 
    COUNT(s.id) AS number_of_orders
FROM 
    employee e
LEFT JOIN 
    sale s ON e.id = s.employee_id
GROUP BY 
    e.name
ORDER BY 
    number_of_orders DESC;

-- Query 2: Total quantity of each item sold
SELECT 
    i.name AS item_name, 
    SUM(si.amount) AS total_quantity_sold
FROM 
    item i
LEFT JOIN 
    sold_item si ON i.id = si.item_id
GROUP BY 
    i.name
ORDER BY 
    total_quantity_sold DESC;

-- Query 3: Top 5 best-selling sellable items
SELECT 
    sel.name AS sellable_name, 
    COUNT(ss.id) AS number_of_sales
FROM 
    sellable sel
LEFT JOIN 
    sold_sellable ss ON sel.id = ss.sellable_id
GROUP BY 
    sel.name
ORDER BY 
    number_of_sales DESC
LIMIT 5;

-- Query 4: Employee access levels and their count
SELECT 
    e.access_level, 
    COUNT(e.id) AS employee_count
FROM 
    employee e
GROUP BY 
    e.access_level
ORDER BY 
    access_level;

-- Query 5: Items with the highest additional price
SELECT 
    i.name AS item_name, 
    i.add_price
FROM 
    item i
ORDER BY 
    i.add_price DESC
LIMIT 10;

-- Query 6: All employees and their managers
SELECT 
    e.name AS employee_name, 
    m.name AS manager_name
FROM 
    employee e
LEFT JOIN 
    employee m ON e.manager_id = m.id
ORDER BY 
    employee_name;

-- Query 7: All the managers
SELECT 
    DISTINCT m.id AS manager_id, 
    m.name AS manager_name, 
    m.job_position AS manager_position
FROM 
    employee e
JOIN 
    employee m ON e.manager_id = m.id
ORDER BY 
    m.id;

-- Query 8: Different Drink Sales
SELECT 
    sel.name AS drink_size, 
    COUNT(ss.id) AS occurrences
FROM 
    sellable sel
JOIN 
    sold_sellable ss ON sel.id = ss.sellable_id
WHERE 
    sel.name IN ('Small Drink', 'Medium Drink', 'Large Drink')
GROUP BY 
    sel.name
ORDER BY 
    occurrences DESC;

-- Query 9: Total number of employees by job position
SELECT 
    e.job_position, 
    COUNT(e.id) AS employee_count
FROM 
    employee e
GROUP BY 
    e.job_position
ORDER BY 
    employee_count DESC;

--Query 10: Top 10 days with least sales
SELECT 
    DATE(s.order_date::timestamp) AS sale_date, 
    SUM(s.total_price) AS total_sales
FROM 
    sale s
GROUP BY 
    sale_date
ORDER BY 
    total_sales ASC
LIMIT 10;

-- Query 11: List of items and their prices from the sellable table
SELECT 
    sel.name AS sellable_name, 
    sel.price AS sellable_price
FROM 
    sellable sel
ORDER BY 
    sellable_price DESC;