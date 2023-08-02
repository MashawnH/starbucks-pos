Midterm logs

hibernate dialect issue
Error executing DDL "create table order
Solution: Change class name 'Order' to 'DrinkOrder' since 'order' is a keyword in mysql;
    thus, the auto-generation of the table would have the wrong syntax

Duplicate entries of register ID in db issue
Had a problem with placing an order at a register twice which should not be allowed.
Solution: check if the register had an order before creating new order

Deleting order by register ID
I had a problem with creating a delete method in OrderRepository  with the first solution involving a custom 
query.
Solution: Use a derived query method as shown here. https://www.baeldung.com/spring-data-jpa-delete#derived-delete

'mvn package' failed tests
Having a problem with tests failing when using mvn package
Solution: Use 'mvn package -Dmaven.test.skip' to skip tests