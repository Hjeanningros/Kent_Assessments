# Create a class named Account with account_number and balance as instance
# attributes. Add a constructor that expects values for both attributes.

from abc import (
  ABC,
  abstractmethod,
)

class Account(ABC):

    def __init__(self, account_number, balance):
        self.__account_number__ = account_number
        self.__balance__ = balance

    def deposit(self, amount):
        if amount > 0:
            self.balance += amount
        
    def withdraw(self, amount):
        if amount > 0 and amount < self.balance:
            self.balance -= amount

    @property
    def account_number(self):
        return self.__account_number__

    @property
    def balance(self):
        return self.__balance__

    @abstractmethod
    def description(self):
        if isinstance(self, SavingsAccount) == True:
            return "current"
        else:
            return "savings"




class SavingsAccount(Account):
    def __init__(self, account_number, balance, interest=0.02):
        super().__init__(account_number, balance)
        self.__interest__ = interest

    def annual_interest(self):
        return self.balance * self.__interest__
    
    def description(self):
        return "savings"


class CurrentAccount(Account):
    def __init__(self, account_number, balance, overdraft=100):
        super().__init__(account_number, balance)
        self.__overdraft__ = overdraft
    
    def withdraw(self, amount):
        if amount > 0 and amount < self.balance + self.overdraft:
            self.balance -= amount

    def description(self):
        return "current"



accounts = [CurrentAccount(1, 100), SavingsAccount(2, 50), SavingsAccount(3, 10), CurrentAccount(4, 200)]

count = 0
for account in accounts:
    if account.description() == "current":
        if account.__overdraft__ > account.balance:
            count += 1
