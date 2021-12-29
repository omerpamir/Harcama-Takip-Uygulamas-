# Expense Calculator

Expense calculator is a simple android application which calculates the expenses spent by the user each month. It calculates monthly and individual expenses, based on the income of the month. The user also has the ability to mark expense as regular or non-regular and show statistics based on the spending data.

This project structure contains four packages namely: adapters, db, utils & view.
Package view contains the activities and custom views.
Package adapter contains the custom adapters that are used by the lists.
Package db contains the classes used for  SQLite database operations and the model classes that are used to get data and set data.
Package utils contain constant values that are globally used.

### HomeActivity.java (package: view)

This is the launcher activity. It contains a view for overall totals at the top, a list, and a button. 
The view at the top displays the overall surplus/deficit and the total income of all the months combined. It changes color based on if it is surplus or deficit. 
The list contains several rows stacked vertically. Each of these rows represents the data for a particular month of a year. It shows the month name, year, total income and the surplus/deficit for that month.
The round button at the bottom right, when pressed shows a dialog which prompts the user to enter values to add a new sheet. 

##### Methods used:

###### onCreate()
Here all the views are initialized and linked to layout files. The method fetchSheetsfromDB() is called when the application launches. 

###### fetchSheetsfromDB()
There are two database queries here. One is to get the list of all sheets that are created. The other query returns total expense for all month combined.  This data is then used to compute the total surplus/deficit value. It is responsible for displaying the overall surplus/deficit and total income data. For ease understanding the green text “Surplus” changes to red colored “Deficit”  if total expense exceeds the total income. If the list is empty, an info message is shown.

###### showAddNewSheetDialog()
This method is called when the user clicks the ‘+’ button at bottom right of the screen. It displays a dialog box prompting the user to enter data to add a new sheet. The user is required to choose the month and year from the drop down and the income for the month is taken in Euro. All these fields are mandatory. The maximum length of income is capped to 20 digits. Clicking the add button will add this entry to the sheets table in SQLite database.

###### onResume()
When the user returns after adding a new sheet, the list needs to update. Thus, in this method, fetchSheetsFromDb() is called again to update the list to include the newly added sheets.


### MonthDetailActivity.java (package: view)

This activity is launched when the user clicks any of the list items in HomeActivity class. It displays the data for the particular month that was clicked. This activity contains a list, a button and a view for showing overall monthly info. The list displays the individual expenses. The info contains total surplus/deficit for the month, the income for the month which is editable. It also displays a pie chart that shows separate totals for regular and non-regular expenses.

##### Methods used:

###### onCreate()
Here all the views are initialized and linked to layout files. The method fetchIncomeFromDB() and fetchExpenseFromDB() is called when the application launches. 

###### fetchIncomeFromDB()
Here,  an SQLite database call is made by passing the month id which returns the data for the requested month. This data is then displayed to the user.

###### fetchExpenseFromDB()
This method fetches the list of all expenses occurred for the particular month. It shows the expense amount, description, a tag to represent if the expense is regular or not and the date.

###### showEditDeleteDialog()
When the user clicks the list item, this method is called, it gives the user two options, either to edit or delete the current expense.

###### showEditIncomeDialog()
Clicking on income text will call this method. It displays a dialog to edit the current income.

###### showExpenseStatistics()
This method calls the function to draw the pie chart.

###### onResume()
When the user returns after adding a new expense, the list needs to update. Thus, here methods fetchIncomeFromDB() and fetchExpenseFromDB() are called again to update the list and compute values based on new expense data.

###### onOptionsItemSelected()
This method is invoked when the user clicks the back button on the toolbar. It allows the user to navigate back.


### AddExpenseActivity.java (package: view)

This activity is called from MonthDetailActivity when the user clicks the “+” button at the bottom. It allows the user to input data for the expense occurred. The user has the can add the amount, a short description, date and even mark expense as regular or non-regular. This class is used to add and also edit the expense.

##### Methods used:

###### onCreate()
Here all the views are initialized and linked to layout files. This method checks if the user wants to edit the expense or add the expense and displays appropriate views.

###### getMonthNumber()
This method returns the month name when a month number is inputted. This is used as the month data is stored as an integer in the SQLite database.

###### validate()
This method validates the data entered the text fields.

###### onOptionsItemSelected()
This method is invoked when the user clicks the back button on the toolbar. It allows the user to navigate back.



### ExpenseListAdapter.java (package: adapters)

This custom class extends BaseAdapter class. This class is used as the adapter for the list which displays individual expenses. It uses a custom row item for the view. This is used as it would allow for more info to be displayed to the user other than simple text.

##### Methods used:

###### ExpenseListAdapter()
This is the constructor method that is being called from the activity.

###### updateExpenseList()
This method updates the ArrayList when new data are added.

###### getCount()
It returns the total count of the expense list

###### getItem()
It returns the expense item based on the position clicked

###### getItemId()
It returns the expense’s id based on the position clicked

###### getView()
It returns the view which displays the list item.

###### isRegular()
It returns the string Regular or non-regular based on a boolean value stored in the database.

### MonthlyListAdapter.java (package: adapters)
This custom class extends BaseAdapter class. This class is used as the adapter for the list which displays month data. It uses a custom row item for the view. This is used as it would allow for more info to be displayed to the user other than simple text.

##### Methods used:

###### MonthlyListAdapter()
This is the constructor method that is being called from the activity.

###### updateExpenseList()
This method updates the ArrayList when new data are added.

###### getCount()
It returns the total count of the month list

###### getItem()
It returns the month item based on the position clicked

###### getItemId()
It returns the month’s id based on the position clicked

###### getView()
It returns the view which displays the list item.

### ExpenseDB.java (package: db)

###### ExpenseDB()
This is the constructor method that is being called from the activity

###### onCreate()
It creates two tables, one for the sheets and other for expenses.

###### onUpgrade()
It drops table when database is updated

###### addNewSheet()
Insert values to table to add new sheets

###### getExpenses()
It returns the list of all expense through the query

###### getIncomeData()
It returns the list of all sheets through the query.

###### editIncome()
It uses the update query to update income

###### updateExpense()
It uses the update the individual expense

###### getSheets()
It returns the list all sheets.

###### addExpense()
A database call to add an expense

###### deleteExpense()
A database call to delete an expense

###### getTotalExpenseMonthly()
A database call to get the total expense of the months

###### getOverallTotalExpense()
A database call to get the overall total expense of all the months

###### checkIfSheetExists()
It returns true if a sheet with a month and year is already added.

###### getBooleanFormat()
Returns boolean value

###### getMonthName()
It returns string name of month.


### AppConstants.java (package: utils)

This class holds static constant values that are globally used throughout the application. The currency symbol used here is of Euro.