SELECT 
    DATE(order_date::timestamp) AS sale_date,
    SUM(total_price) AS total_sales
FROM 
    sale 
GROUP BY 
    sale_date
ORDER BY 
    total_sales DESC
LIMIT 10;