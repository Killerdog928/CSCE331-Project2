import psycopg2
import random
from datetime import datetime, timedelta

# Connect to the PostgreSQL database
conn = psycopg2.connect(
    host="csce-315-db.engr.tamu.edu",
    port="5432",
    user="csce331_24",
    password="cracked.donkey",  # replace with your actual password
    database="csce331_24"
)
#open a .sql file to write to

def get_random_item():
    cur = conn.cursor()
    cur.execute("SELECT * FROM item")
    items = cur.fetchall()
    cur.close()  # Close the cursor, not the connection
    return random.choice(items)


# Fill in the employee table
def fill_employee_table():
    #clear the current table if it exists
    cur = conn.cursor()
    #cur.execute("DELETE FROM employee")
    #conn.commit()
    f = open("fill_employee_table.sql", "w")
    random_names = [
        "John Doe", "Jane Doe", "Alice Smith", "Bob Smith",
        "Charlie Brown", "Lucy Brown", "Snoopy Dog",
        "Linus Van Pelt", "Peppermint Patty", "Marcie Johnson"
    ]

    # Connect to the database using the existing connection
    cur = conn.cursor()
    #select 3 employee ids from 1 to 10 at random to be managers, and one of those 3 to be head manager
    manager_ids = random.sample(range(1, 11), 3)
    head_manager_id = random.choice(manager_ids)
    # Insert the values into the employee table
    for i in range(10):
        #if this employee is a manager, set their access level to 3, otherwise set it to 1 or 2
        access_level = random.choice([1, 2])
        title = 'cashier'
        #random hire date between jan 1 2022 and current date
        days_since_2022 = (datetime.now() - datetime(2022, 1, 1)).days
        start_day = (datetime.now() - timedelta(days=random.randint(0,days_since_2022))).strftime("%Y-%m-%d")
        manager_id = 1
        if i+1 in manager_ids:
            access_level = 3
            title = 'manager'
        if i+1 == head_manager_id:
            title = 'head manager'
        #cur.execute("INSERT INTO employee (id, name, start_day, job_position, access_level, manager_id) VALUES (%s, %s, %s, %s, %s, %s)",(i + 1, random_names[i], random_days[i], title, access_level, 1))
        #instead of executing, save the commands into a sql file to run at a later time
        command = "INSERT INTO employee (id, name, start_day, job_position, access_level, manager_id) VALUES (" + str(i + 1) + ", '" + random_names[i] + "', '" + start_day + "', '" + title + "', " + str(access_level) + ", " + str(manager_id) + ");\n"
        f.write(command)
    #fix manager_id to be a random manager for regular employees and the head manager for managers
    for i in range(10):
        if i+1 in manager_ids:
            manager_id = i+1
        else:
            manager_id = random.choice(manager_ids)
        command = "UPDATE employee SET manager_id = " + str(manager_id) + " WHERE id = " + str(i + 1) + ";\n"
        f.write(command)

    # Commit the changes to the database
    #conn.commit()
    #conn.commit()
    cur.close()  # Close the cursor after operations are complete
    f.close()

def create_sold_item():
    #sold_item has an id, item_id, sold_sellable_id, and amount
    #sold_sellables will hold an amount of sold_items based on their types. Appetizers (id 12) hold 1 (with an item id selected from 17-20), bowls (id number 1) 2 (1 from 1 to 12 and another from 13 to 16), plates (id number 2) 3 (2 from 1 to 12 and 1 from 13 to 16), bigger plates (id number 3) 4 (3 from 1 to 12 and 1 from 13 to 16)
    #small, medium and large entrees (id 7,8,9) will hold 1, 2, and 3 sold_items respectively, with the item id selected from 1 to 12
    #small, medium, and large drinks (4, 5, 6) will hold no sold_items, so skip them
    #medium and large sides (id 10, 11) will hold 1 and 2 sold_items respectively, with the item id selected from 13 to 16

    cur = conn.cursor()
    cur.execute("SELECT * FROM item")
    items = cur.fetchall()
    cur.execute("SELECT * FROM sold_sellable")
    sold_sellables = cur.fetchall()
    cur.close()
    f = open("fill_sold_item_table.sql", "w")
    commands = []
    sellable_count = len(sold_sellables)
    #each sold_item has a id, item_id, sold_sellable_id, and amount
    #Have amount be 1 for everything for now
    #follow the steps above for everything else.
    item_index = 1
    for i in range(sellable_count):
        #check the sellable_id to determine how many items to assign to this sold_sellable
        sellable_type = sold_sellables[i][2]
        if(sellable_type == 1):
            #bowl
            item_id1 = random.randint(1, 12)
            item_id2 = random.randint(13, 16)
            command = "INSERT INTO sold_item (id, item_id, sold_sellable_id, amount) VALUES (" + str(item_index) + ", " + str(item_id1) + ", " + str(i + 1) + ", 1);\n"
            commands.append(command)
            item_index += 1
            command = "INSERT INTO sold_item (id, item_id, sold_sellable_id, amount) VALUES (" + str(item_index) + ", " + str(item_id2) + ", " + str(i + 1) + ", 1);\n"
            commands.append(command)
            item_index += 1
        elif(sellable_type == 2):
            #plate
            item_id1 = random.randint(1, 12)
            item_id2 = random.randint(1, 12)
            item_id3 = random.randint(13, 16)
            command = "INSERT INTO sold_item (id, item_id, sold_sellable_id, amount) VALUES (" + str(item_index) + ", " + str(item_id1) + ", " + str(i + 1) + ", 1);\n"
            commands.append(command)
            item_index += 1
            command = "INSERT INTO sold_item (id, item_id, sold_sellable_id, amount) VALUES (" + str(item_index) + ", " + str(item_id2) + ", " + str(i + 1) + ", 1);\n"
            commands.append(command)
            item_index += 1
            command = "INSERT INTO sold_item (id, item_id, sold_sellable_id, amount) VALUES (" + str(item_index) + ", " + str(item_id3) + ", " + str(i + 1) + ", 1);\n"
            commands.append(command)
            item_index += 1
        elif(sellable_type == 3):
            #bigger plate
            item_id1 = random.randint(1, 12)
            item_id2 = random.randint(1, 12)
            item_id3 = random.randint(1, 12)
            item_id4 = random.randint(13, 16)
            command = "INSERT INTO sold_item (id, item_id, sold_sellable_id, amount) VALUES (" + str(item_index) + ", " + str(item_id1) + ", " + str(i + 1) + ", 1);\n"
            commands.append(command)
            item_index += 1
            command = "INSERT INTO sold_item (id, item_id, sold_sellable_id, amount) VALUES (" + str(item_index) + ", " + str(item_id2) + ", " + str(i + 1) + ", 1);\n"
            commands.append(command)
            item_index += 1
            command = "INSERT INTO sold_item (id, item_id, sold_sellable_id, amount) VALUES (" + str(item_index) + ", " + str(item_id3) + ", " + str(i + 1) + ", 1);\n"
            commands.append(command)
            item_index += 1
            command = "INSERT INTO sold_item (id, item_id, sold_sellable_id, amount) VALUES (" + str(item_index) + ", " + str(item_id4) + ", " + str(i + 1) + ", 1);\n"
            commands.append(command)
            item_index += 1
        elif(sellable_type == 7):
            #small entree
            item_id1 = random.randint(1, 12)
            command = "INSERT INTO sold_item (id, item_id, sold_sellable_id, amount) VALUES (" + str(item_index) + ", " + str(item_id1) + ", " + str(i + 1) + ", 1);\n"
            commands.append(command)
            item_index += 1
        elif(sellable_type == 8):
            #medium entree
            item_id1 = random.randint(1, 12)
            item_id2 = random.randint(1, 12)
            command = "INSERT INTO sold_item (id, item_id, sold_sellable_id, amount) VALUES (" + str(item_index) + ", " + str(item_id1) + ", " + str(i + 1) + ", 1);\n"
            commands.append(command)
            item_index += 1
            command = "INSERT INTO sold_item (id, item_id, sold_sellable_id, amount) VALUES (" + str(item_index) + ", " + str(item_id2) + ", " + str(i + 1) + ", 1);\n"
            commands.append(command)
            item_index += 1
        elif(sellable_type == 9):
            #large entree
            item_id1 = random.randint(1, 12)
            item_id2 = random.randint(1, 12)
            item_id3 = random.randint(1, 12)
            command = "INSERT INTO sold_item (id, item_id, sold_sellable_id, amount) VALUES (" + str(item_index) + ", " + str(item_id1) + ", " + str(i + 1) + ", 1);\n"
            commands.append(command)
            item_index += 1
            command = "INSERT INTO sold_item (id, item_id, sold_sellable_id, amount) VALUES (" + str(item_index) + ", " + str(item_id2) + ", " + str(i + 1) + ", 1);\n"
            commands.append(command)
            item_index += 1
            command = "INSERT INTO sold_item (id, item_id, sold_sellable_id, amount) VALUES (" + str(item_index) + ", " + str(item_id3) + ", " + str(i + 1) + ", 1);\n"
            commands.append(command)
            item_index += 1
        elif(sellable_type == 10):
            #medium side
            item_id1 = random.randint(13, 16)
            command = "INSERT INTO sold_item (id, item_id, sold_sellable_id, amount) VALUES (" + str(item_index) + ", " + str(item_id1) + ", " + str(i + 1) + ", 1);\n"
            commands.append(command)
            item_index += 1
        elif(sellable_type == 11):
            #large side
            item_id1 = random.randint(13, 16)
            item_id2 = random.randint(13, 16)
            command = "INSERT INTO sold_item (id, item_id, sold_sellable_id, amount) VALUES (" + str(item_index) + ", " + str(item_id1) + ", " + str(i + 1) + ", 1);\n"
            commands.append(command)
            item_index += 1
            command = "INSERT INTO sold_item (id, item_id, sold_sellable_id, amount) VALUES (" + str(item_index) + ", " + str(item_id2) + ", " + str(i + 1) + ", 1);\n"
            commands.append(command)
            item_index += 1
        elif(sellable_type == 12):
            #appetizer
            item_id1 = random.randint(17, 20)
            command = "INSERT INTO sold_item (id, item_id, sold_sellable_id, amount) VALUES (" + str(item_index) + ", " + str(item_id1) + ", " + str(i + 1) + ", 1);\n"
            commands.append(command)
            item_index += 1
    for command in commands:
        f.write(command)
    f.close()
def create_sold_sellable():
    #read from sold_item table to create sold_sellable table
    #assign 1 sellable 70% of the time, 2 sellables 20%, and 3 sellables 10% to an array, then use that to determine sale_id
    #it has an id, sale_id, and sellable_id (read from sellable table)
    cur = conn.cursor()
    cur.execute("SELECT * FROM sellable")
    sellables = cur.fetchall()
    cur.execute("SELECT * FROM sale")
    sales = cur.fetchall()
    cur.close()
    f = open("fill_sold_sellable_table.sql", "w")
    commands = []
    #each sold_item has a id, sale_id, and sellable_id.
    #for each sale, pick a sale_id from 1 to 3 at random, then do a 40% chance of picking a sale_id from 4 to 6, a 20% chance of picking a sale_id from 7 to 11, and a 30% chance of picking a sale_id of 12
    #based on the number of rolls, set up bools telling us how many sellables to assign to this sale
    entree_types = []
    drink_types = []
    extra_types = []
    side_types = []
    #put 0 in if the item is not of that type, the id of the item if it is
    for i in range(10000):
        #distinct rolls for each
        roll1 = random.random()
        roll2 = random.random()
        roll3 = random.random()
        roll4 = random.random()
        if roll1 < 1/3:
            entree_types.append(1)
        elif roll1 < 2/3:
            entree_types.append(2)
        else:
            entree_types.append(3)
        if roll2 < .4:
            roll21 = random.random()
            if roll21 < 1/3:
                drink_types.append(4)
            elif roll21 < 2/3:
                drink_types.append(5)
            else:
                drink_types.append(6)
        else:
            drink_types.append(0)
        if roll3 < .2:
            roll31 = random.random()
            if roll31 < 1/5:
                extra_types.append(7)
            elif roll31 < 2/5:
                extra_types.append(8)
            elif roll31 < 3/5:
                extra_types.append(9)
            elif roll31 < 4/5:
                extra_types.append(10)
            else:
                extra_types.append(11)
        else:
            extra_types.append(0)
        if roll4 < .3:
            side_types.append(12)
        else:
            side_types.append(0)
    #compute how many sellables we have total
    total_sellables = 0
    for i in range(10000):
        if entree_types[i] != 0:
            total_sellables += 1
        if drink_types[i] != 0:
            total_sellables += 1
        if extra_types[i] != 0:
            total_sellables += 1
        if side_types[i] != 0:
            total_sellables += 1
    sellable_id = 1
    for i in range(10000):
        #first append the entree for any given sale
        if entree_types[i] != 0:
            command = "INSERT INTO sold_sellable (id, sale_id, sellable_id) VALUES (" + str(sellable_id) + ", " + str(i + 1) + ", " + str(entree_types[i]) + ");\n"
            commands.append(command)
            sellable_id += 1
        #then append the drink
        if drink_types[i] != 0:
            command = "INSERT INTO sold_sellable (id, sale_id, sellable_id) VALUES (" + str(sellable_id) + ", " + str(i + 1) + ", " + str(drink_types[i]) + ");\n"
            commands.append(command)
            sellable_id += 1
        #then append the extra
        if extra_types[i] != 0:
            command = "INSERT INTO sold_sellable (id, sale_id, sellable_id) VALUES (" + str(sellable_id) + ", " + str(i + 1) + ", " + str(extra_types[i]) + ");\n"
            commands.append(command)
            sellable_id += 1
        #then append the side
        if side_types[i] != 0:
            command = "INSERT INTO sold_sellable (id, sale_id, sellable_id) VALUES (" + str(sellable_id) + ", " + str(i + 1) + ", " + str(side_types[i]) + ");\n"
            commands.append(command)
            sellable_id += 1
    for command in commands:
        f.write(command)
    f.close()
def create_sales():
    #create 10000 sales from jan 2022 to current date, randomly assigning an employee to each sale and random timesteps within the range
    cur = conn.cursor()
    #each sale has a id (shared with item, so randomly select an id from the item table), employee_id, total_price (for now just set randomly between 7.99, 9.99, and 11.99), and order_date (randomly select a date between jan 1 2022 and current date)
    #after generating all these sales, sort them by order_date, and create a sql query to insert them into the sales table
    #save this query to a file to run at a later time
    #grab all employee ids not assigned to managers
    cur.execute("SELECT id FROM employee WHERE access_level < 3")
    employees = cur.fetchall()
    cur.close()
    f = open("fill_sales_table.sql", "w")
    commands = []
    for i in range(10000):
        employee_id = random.choice(employees)[0]
        total_price = random.choice([7.99, 9.99, 11.99])
        #order_date is a timestamp with date and time to the second between jan 1 2022 and current date
        days_since_2022 = (datetime.now() - datetime(2022, 1, 1)).days
        order_date = (datetime.now() - timedelta(days=random.randint(0, days_since_2022))-timedelta(seconds=random.randint(0, 86400))).strftime("%Y-%m-%d %H:%M:%S")
        command = "INSERT INTO sale (id, employee_id, total_price, order_date) VALUES (" + str(i + 1) +  ", " + str(employee_id) + ", " + str(total_price) + ", '" + order_date + "');\n"
        commands.append(command)
    #choose two random dates and add an extra 500 sales to each of those dates (testing purposes)
    date1 = (datetime.now() - timedelta(days=random.randint(0, days_since_2022))).strftime("%Y-%m-%d")
    date2 = (datetime.now() - timedelta(days=random.randint(0, days_since_2022))).strftime("%Y-%m-%d")
    for i in range(500):
        employee_id = random.choice(employees)[0]
        total_price = random.choice([7.99, 9.99, 11.99])
        hours = random.randint(0, 23)
        minutes = random.randint(0, 59)
        seconds = random.randint(0, 59)
        #make them always 2 digit strings
        if hours < 10:
            hours = "0"+str(hours)
        if minutes < 10:
            minutes = "0"+str(minutes)
        if seconds < 10:
            seconds = "0"+str(seconds)

        order_date = date1+" "+str(hours)+":"+str(minutes)+":"+str(seconds )
        command = "INSERT INTO sale (id,  employee_id, total_price, order_date) VALUES (" + str(i + 10000) + ", " + str(employee_id) + ", " + str(total_price) + ", '" + order_date + "');\n"
        commands.append(command)
        employee_id = random.choice(employees)[0]
        total_price = random.choice([7.99, 9.99, 11.99])
        hours = random.randint(0, 23)
        minutes = random.randint(0, 59)
        seconds = random.randint(0, 59)
        # make them always 2 digit strings
        if hours < 10:
            hours = "0" + str(hours)
        if minutes < 10:
            minutes = "0" + str(minutes)
        if seconds < 10:
            seconds = "0" + str(seconds)

        order_date = date2 + " " + str(hours) + ":" + str(minutes) + ":" + str(seconds)
        command = "INSERT INTO sale (id, employee_id, total_price, order_date) VALUES (" + str(i + 10500) + ", " + str(employee_id) + ", " + str(total_price) + ", '" + order_date + "');\n"
        commands.append(command)
    for command in commands:
        f.write(command)
    f.close()
    

# Call the function to fill the employee table
#fill_employee_table()
#create_sales()
#create_sold_sellable()
#create_sold_item()
# Optionally, close the connection when done
conn.close()
