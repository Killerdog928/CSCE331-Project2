SELECT 
    DATE_TRUNC('week', order_date) AS week_start,
    COUNT(*) AS entry_count
FROM 
    sale
WHERE 
    order_date >= NOW() - INTERVAL '1 year'
GROUP BY 
    week_start
ORDER BY 
    week_start;
