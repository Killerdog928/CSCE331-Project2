SELECT 
    EXTRACT(HOUR FROM s.order_date::timestamp) AS sale_hour, 
    SUM(s.total_price) AS total_sales
FROM 
    sale s
WHERE 
    EXTRACT(HOUR FROM s.order_date::timestamp) BETWEEN 10 AND 21
GROUP BY 
    sale_hour
ORDER BY 
    sale_hour;





