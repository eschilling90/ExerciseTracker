from google.appengine.ext import ndb

import logging

class Expense(ndb.Model):

	itemId = ndb.IntegerProperty()
	username = ndb.StringProperty()
	location = ndb.StringProperty()
	itemName = ndb.StringProperty()
	price = ndb.FloatProperty()
	purchaseDate = ndb.DateProperty(auto_now_add=True)
	category = ndb.StringProperty()
	purchaseGroupId = ndb.IntegerProperty()
	purchaseGroupTotal = ndb.FloatProperty()
	purchaseGroupCategory = ndb.StringProperty()
	lat = ndb.FloatProperty(default=30.267153)
	lon = ndb.FloatProperty(default=-97.743061)

	@staticmethod
	def getNextItemId():
		maxId = 0
		for expense in Expense.query():
			if expense.itemId > maxId:
				maxId = expense.itemId
		return maxId + 1

	@staticmethod
	def getNextGroupId():
		maxId = 0
		for expense in Expense.query():
			if expense.purchaseGroupId > maxId:
				maxId = expense.purchaseGroupId
		return maxId + 1