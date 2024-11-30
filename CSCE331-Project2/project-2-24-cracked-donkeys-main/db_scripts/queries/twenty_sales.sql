SELECT 
    s.id AS sale_id,
    sel.name AS sellable_name,
    STRING_AGG(i.name, ', ') AS item_names
FROM 
    sale s
LEFT JOIN 
    sold_sellable ss ON s.id = ss.sale_id
LEFT JOIN 
    sellable sel ON ss.sellable_id = sel.id
LEFT JOIN 
    sold_item si ON ss.id = si.sold_sellable_id
LEFT JOIN 
    item i ON si.item_id = i.id
GROUP BY 
    s.id, sel.name
LIMIT 20;