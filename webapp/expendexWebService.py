import webapp2
import json
import urllib
import logging
import time
import datetime
import re
import random

from google.appengine.api import urlfetch
from google.appengine.api import mail
from google.appengine.ext import ndb
from google.appengine.ext import db
from google.appengine.ext import blobstore
from google.appengine.ext.webapp import blobstore_handlers
from google.appengine.api import files, images

from ExpendexUser import User as eUser
from ExpendexExpense import Expense as eExpense

class LoginUser(webapp2.RequestHandler):
	def get(self):
		self.response.write('LoginUser')

	def post(self):
		statusCode =0
		username = self.request.get('username')
		password = self.request.get('password')
		user = eUser.query(eUser.username == str(username)).get()
		if user and user.password == password:
			statusCode =1

		elif user and user.password != password:
			statusCode = 2

		else:
			statusCode = 3

		self.response.write(json.dumps({'status_code': statusCode}))

class RegisterUser(webapp2.RequestHandler):
	def get(self):
		self.response.write('Register User')

	def post(self):
		statusCode = 0
		name = self.request.get('name')
		username = self.request.get('username')
		password = self.request.get('password')
		newUser = eUser(name = name, username = username, password = password,reportRate = 0, budget=[])
		if newUser:
			newUser.put()
			statusCode = 1

		self.response.write(json.dumps({'status_code': statusCode}))

class AddExpense(webapp2.RequestHandler):
	def get(self):
		self.response.write('NewExpense')

	def post(self):
		logging.info(self.request.url)
		logging.info(self.request.query_string)
		logging.info(self.request.body)
		logging.info(json.loads(self.request.body)['location'])
		params = json.loads(self.request.body)
		try:
			username = params['username']
		except KeyError:
			username = 'cl2user@gmail.com'
		try:
			location = params['location']
		except KeyError:
			location = "None"
		try:
			total = params['total']
		except KeyError:
			total = 0.0
		try:
			groupCategory = params['groupCategory']
		except KeyError:
			groupCategory = "None"
		try:
			expenseList = params['expenseList']
		except KeyError:
			expenseList = []
		groupId = eExpense.getNextGroupId()
		logging.info(expenseList)
		status_code = 0
		AddExpense.checkBudget(groupCategory, total, username)
		for expense in expenseList:
			itemId = eExpense.getNextItemId()
			lat = float(expense['lat'])
			if lat == 0.0:
				lat = 30.267152
			lon = float(expense['lon'])
			if lon == 0.0:
				lon = -97.743060
			newExpense = eExpense(itemId=itemId,username=username,location=location,price=expense['price'],category=expense['category'],itemName=expense['itemName'],purchaseGroupId=groupId,purchaseGroupTotal=total,purchaseGroupCategory=groupCategory,lat=lat,lon=lon)
			newExpense.put()
			status_code = 1
		self.response.write(json.dumps({'status_code': status_code}))

	@staticmethod
	def checkBudget(category, total, username):
		user = eUser.query(eUser.username==username).get()
		if user:
			budget = user.budget
			limit = 0
			totalLimit = 0
			for b in budget:
				if b.split("|")[0] == category:
					limit = int(b.split("|")[1])
				if b.split("|")[0] == "Total":
					totalLimit = int(b.split("|")[1])
			if limit > 0 or totalLimit > 0:
				year = date[0]
				month = date[0]
				visitedGroups = []
				tempCatTotal = 0
				tempTotal = 0
				date = []
				for i in datetime.datetime.now().timetuple():
					date.append(int(i))
				curYear = date[0]
				curMonth = date[1]
				for expense in eExpense.query(eExpense.username==str(username)):
					if not expense.purchaseGroupId in visitedGroups:
						visitedGroups.append(expense.purchaseGroupId)
						expenseDate = []
						for i in expense.purchaseDate.timetuple():
							expenseDate.append(int(i))
						eYear = expenseDate[0]
						eMonth = expenseDate[1]
						if eYear == curYear and eMonth == curMonth:
							if expense.purchaseGroupCategory == category:
								tempCatTotal = tempCatTotal + expense.purchaseGroupTotal
							tempTotal = tempTotal + expense.purchaseGroupTotal
				if tempCatTotal + total >= limit and limit > 0:
					if (mail.is_email_valid(username)):
						message = mail.EmailMessage(sender="erik.schilling@gmail.com",subject="Budget Reached")
						message.to = username
						message.body = """
						Dear Expendex User:
						You have reached or exceeded your budget of ${0} for {1}
						""".format(limit, category)
						message.body = message.body + """
						Please let us know if you have any questions.
						The Expendex Team
						"""
						message.send()
				if tempTotal + total >= totalLimit and totalLimit > 0:
					if (mail.is_email_valid(username)):
						message = mail.EmailMessage(sender="erik.schilling@gmail.com",subject="Budget Reached")
						message.to = username
						message.body = """
						Dear Expendex User:
						You have reached or exceeded your TOTAL budget of ${0}
						""".format(limit, category)
						message.body = message.body + """
						Please let us know if you have any questions.
						The Expendex Team
						"""
						message.send()


class DeleteItem(webapp2.RequestHandler):
	def post(self):
		status_code = 0
		itemId = int(self.request.get('itemId'))
		expense = eExpense.query(eExpense.itemId == itemId).get()
		if expense:
			expense.key.delete()
			status_code=1


		self.response.write(json.dumps({'status_code': status_code}))

class DeleteExpense(webapp2.RequestHandler):
	def get(self):
		self.response.write("Delete Expense")

	def post(self):
		status_code = 0
		groupId = int(self.request.get('purchaseGroupId'))
		expenseList = eExpense.query(eExpense.purchaseGroupId == groupId)
		for expense in expenseList:
			expense.key.delete()
			status_code=1


		self.response.write(json.dumps({'status_code': status_code}))
		
class ViewAllExpenses(webapp2.RequestHandler):
	def post(self):
		expenseList = []
		logging.info(self.request.body)
		logging.info(json.loads(self.request.body))
		username = json.loads(self.request.body) #should just be the username (not even a tag for 'username')
		dthandler = lambda obj: (
			obj.isoformat()
			if isinstance(obj, datetime.datetime)
			or isinstance(obj, datetime.date)
			else None)
		for expense in eExpense.query(eExpense.username == str(username)):
			date = []
			for i in expense.purchaseDate.timetuple():
				date.append(int(i))
			expenseList.append({'itemId': expense.itemId, 'purchaseGroupId': expense.purchaseGroupId, 'year': date[0], 'month': date[1], 'day': date[2], 'itemName': expense.itemName, 'purchaseGroupTotal': expense.purchaseGroupTotal, 'location': expense.location, 'price': expense.price, 'purchaseGroupCategory': expense.purchaseGroupCategory, 'category': expense.category})
		self.response.write(json.dumps(expenseList))

class ViewSingleExpense(webapp2.RequestHandler):
	def post(self):
		expenseList = []
		params = json.loads(self.request.body).split(',')
		username = params[1]
		groupId = int(params[0])
		dthandler = lambda obj: (
			obj.isoformat()
			if isinstance(obj, datetime.datetime)
			or isinstance(obj, datetime.date)
			else None)
		for expense in eExpense.query(eExpense.username == str(username) and eExpense.purchaseGroupId == groupId):
			date = []
			for i in expense.purchaseDate.timetuple():
				date.append(int(i))
			expenseList.append({'itemId': expense.itemId, 'purchaseGroupId': expense.purchaseGroupId, 'year': date[0], 'month': date[1], 'day': date[2], 'itemName': expense.itemName, 'purchaseGroupTotal': expense.purchaseGroupTotal, 'location': expense.location, 'price': expense.price, 'purchaseGroupCategory': expense.purchaseGroupCategory, 'category': expense.category})
		self.response.write(json.dumps(expenseList))

class ViewSingleCategory(webapp2.RequestHandler):
	def post(self):
		expenseList = []
		params = json.loads(self.request.body).split(",")
		category = params[0]
		username = params[1]
		dthandler = lambda obj: (
			obj.isoformat()
			if isinstance(obj, datetime.datetime)
			or isinstance(obj, datetime.date)
			else None)
		for expense in eExpense.query(eExpense.username == str(username) and eExpense.purchaseGroupCategory == str(category)):
			date = []
			for i in expense.purchaseDate.timetuple():
				date.append(int(i))
			expenseList.append({'itemId': expense.itemId, 'purchaseGroupId': expense.purchaseGroupId, 'year': date[0], 'month': date[1], 'day': date[2], 'itemName': expense.itemName, 'purchaseGroupTotal': expense.purchaseGroupTotal, 'location': expense.location, 'price': expense.price, 'purchaseGroupCategory': expense.purchaseGroupCategory, 'category': expense.category})
		self.response.write(json.dumps(expenseList))

class ViewMapClusters(webapp2.RequestHandler):
	def post(self):
		clusterItemList = []
		visitedGroups = []
		username = self.request.get('username')
		for expense in eExpense.query(eExpense.username==str(username)):
			if not expense.purchaseGroupId in visitedGroups:
				visitedGroups.append(expense.purchaseGroupId)
				clusterItemList.append({'purchaseGroupId': expense.purchaseGroupId, 'price': expense.purchaseGroupTotal, 'lat': expense.lat, 'lon': expense.lon, 'location': expense.location})
		self.response.write(json.dumps(clusterItemList))

class SaveBudget(webapp2.RequestHandler):
	def post(self):
		logging.info(json.loads(self.request.body))
		params = json.loads(self.request.body)
		budget = []
		for entry in params:
			budget.append(str(entry['category'])+"|"+str(entry['limit']))
		username = self.request.get('username')
		user = eUser.query(eUser.username==str(username)).get()
		if user:
			user.budget = budget
			user.put()

class GetBudget(webapp2.RequestHandler):
	def post(self):
		username = self.request.get('username')
		budget = []
		for user in eUser.query(eUser.username==str(username)):
			for entry in user.budget:
				category = entry.split("|")[0]
				limit = entry.split("|")[1]
				budget.append({'category':category, 'limit':int(limit)})
		self.response.write(json.dumps(budget))

application = webapp2.WSGIApplication([
	('/loginuser', LoginUser),
	('/registeruser', RegisterUser),
	('/addexpense', AddExpense),
	('/deleteitem',DeleteItem),
	('/deleteexpense',DeleteExpense),
	('/viewallexpenses', ViewAllExpenses),
	('/viewsingleexpense', ViewSingleExpense),
	('/viewsinglecategory', ViewSingleCategory),
	('/viewmapclusters', ViewMapClusters),
	('/savebudget', SaveBudget),
	('/getbudget', GetBudget)
], debug = True)