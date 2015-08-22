#!/usr/bin/env python

import webapp2
import json
import urllib
import logging
import time
import datetime
import re
import random

from google.appengine.api import urlfetch
from google.appengine.ext import ndb

from User import User
from Notification import Notification
from Exercise import Exercise
from Workout import Workout
from Trains import Trains
from Weight import Weight
from UserWeight import UserWeight
from WorkoutContents import WorkoutContents


class LoginHandler(webapp2.RequestHandler):
	def get(self):
		self.response.write('In LoginHandler')

	def post(self):
		statusCode = 202
		emailAddress = self.request.get('emailAddress')
		password = self.request.get('password')
		user = User.query(User.emailAddress == emailAddress).get()
		if user and user.verify(password):
			statusCode = 200
		elif user and not user.verify(password):
			statusCode = 201
		self.response.write(json.dumps({'statusCode': statusCode}))

class RegisterHandler(webapp2.RequestHandler):
	def get(self):
		self.response.write('In RegisterHandler')

	def post(self):
		#status codes:
		#200 = ok
		#201 = email already in use
		#202 = some other error
		statusCode = 202
		firstName = self.request.get('firstName')
		lastName = self.request.get('lastName')
		name = firstName + "#" + lastName
		emailAddress = self.request.get('emailAddress')
		password = self.request.get('password')
		permissions = int(self.request.get('permissions'))
		if permissions == '':
			permissions = 1
		emailCheck = User.query(User.emailAddress == str(emailAddress)).get()
		if emailCheck:
			statusCode = 201
		else:
			newUser = User(name = name, emailAddress = emailAddress, permissionFlag = permissions)
		if newUser:
			newUser.set_dk(password)
			newUser.put()
			statusCode = 200
		self.response.write(json.dumps({'statusCode': statusCode}))

class TrainerHandler(webapp2.RequestHandler):
	def get(self):
		#statusCodes:
		#200 ok
		#201 invalid email
		#202 other error
		statusCode = 202
		getTrainer = self.request.get('getTrainer')
		emailAddress = self.request.get('emailAddress')
		user = User.query(User.emailAddress == emailAddress).get()
		if user:
			statusCode = 201
			if getTrainer != '':
				trainers = []
				for trainerTuple in Trains.query(Trains.traineeEmail == user.emailAddress):
					trainer = User.query(User.emailAddress==trainerTuple.trainerEmail)
					if trainer:
						trainers.append(trainer.getViewableInfo())
						statusCode = 200
						self.response.write(json.dumps({'statusCode': statusCode, 'trainers': trainers}))
			else:
				trainees = []
				for traineeTuple in Trains.query(Trains.trainerEmail == user.emailAddress):
					trainee = User.query(User.emailAddress==traineeTuple.traineeEmail)
					if trainee:
						trainees.append(trainee.getViewableInfo())
						statusCode = 200
						self.response.write(json.dumps({'statusCode': statusCode, 'trainees': trainees}))
		self.response.write(json.dumps({'statusCode': statusCode}))

	def post(self):
		#statusCodes:
		#200 ok
		#201 invalid other address
		#202 invalid user address
		#203 other error
		statusCode = 203
		trainerAddress = self.request.get('trainerAddress')
		traineeAddress = self.request.get('traineeAddress')
		statusCode = 202
		trainer = User.query(User.emailAddress == trainerAddress).get()
		if trainer:
			statusCode = 201
			trainee = User.query(User.emailAddress == traineeAddress).get()
			if trainee:
				train = Trains(trainerAddress, traineeAddress)
				train.put()
				statusCode = 200
		self.response.write(json.dumps({'statusCode': statusCode}))

class NotificationHandler(webapp2.RequestHandler):
	'''
	Each class in here is a web handler. I name each one after whatever it happens to be handling. But that is not required
	and simply by design. You could have one hanlder that only adds stuff to the database and every post request goes to it.
	But that is annyoing to work with. So a reasonably logical design is to break everything up into entity handlers.

	There are 3 main request handlers inside of NotificationHandler: get, post, and delete. These names describe what they
	do pretty well. Get will return requested information. In this case, it returns the notification information for a
	specified user. Post will save stuff to the database. This could mean either inserting or deleteing.
	And delete will delete stuff from the database. More information can be found here (https://cloud.google.com/appengine/docs/python/tools/webapp/requesthandlerclass)
	This site is just a very shallow overview, you are better off following links off of that page/site entirely.

	As far as I can tell, self has two attributes, request and response. Generally, you 'get' from the request attribute
	and 'write' to the response attribute. More detail in the functions.

	https://cloud.google.com/appengine/docs/python/ndb/
	That is a good look to look at for NDB Datastore API information. I suggest you read at least the Overview, entities
	and keys, and properties sections.
	'''
	def get(self):
		'''
		Here I use self.request.get to retrieve the emailAddress URL parameter from the request.
		There are two kinds of parameters we will use: URL parameters and body. URL parameters are the parameters
		that are explicitly part of the URL, everything that comes after the ?. The body is attached to the request
		and I create it from the Java side using GSON. I'll talk more about the body in the post function.

		.get looks for the parameter called emailAddress and sets its value to email. If there is no parameter
		then the default behavior is to return an empty string but you can pass another parameter to .get that is
		the default value. So if you did self.request.get('emailAddress', 'None'), if there was no emailAddress
		parameter then email would equal None.
		'''
		receiverEmail = str(self.request.get('receiverEmail'))
		'''
		SomeEntity.query(SomeEntity.attribute==value) is your way of accessing the database. In this example,
		we are accessing the User table. In SQL this would be SELECT * FROM USER WHERE User.emailAddress=email.
		You can put any conditional inside the (), not just ==. Make sure you do User.attribute or else python 
		comnplains that it can't find the emailAddress variable.
		'''
		user = User.query(User.emailAddress == receiverEmail).get()
		'''
		Here I try to get the notificationId parameter
		'''
		notificationId = self.request.get('notificationId')
		'''
		I am using this as a flag so I know if I should return all of the notifications in the short format
		or one specific notification with detailed format. If I don't find a notificationId parameter (notificationId,
			the python variable, is an empty string since that is the default behavior of .get if it can't find the parameter)
		then I know I just want to return all of the notifications in short format for the inbox. If I do find the
		notificationId then I know I want a specific notification to return in detailed format.
		'''
		if notificationId == '':
			self.getShort(receiverEmail, user)
		else:
			self.getDetail(notificationId)

	'''
	This is a function I made, it has nothing to do with the Handler stuff, just something I made to make it easier.
	You can think of get, post, and delete as functions that can be overridden and getShort and getDetail as just
	helper functions.
	'''
	def getShort(self, recieverEmail, user):
		#statusCodes:
		#200 ok
		#201 other error
		statusCode = 201
		notifications = []
		'''
		I use 'if user' as a check if the user entity actually exists. It is checking if it is null.
		Which, if you check back up in .get is what that function will return if it can't find anything.
		So, if it is not null I know I have something to work with
		'''
		if user:
			statusCode = 200
			'''
			You may have noticed that I said .get after the User.query() function up in get but not here.
			That is because .query returns an iterable list of query objects, not something you can just access
			really well. So you can either do User.query().get() to return the first entry in the list (There are
				ways to get other indexes but I am not sure how). You would only really want to do this if you know
			there is going to be exactly one row returned. Otherwise you can put it into a foreach loop without the
			.get at the end. This lets you iterate through everything where notification is an actual Notification
			object and not some weird query thing.
			'''
			for notification in Notification.query(Notification.receiverEmail == user.emailAddress):
				'''
				I then add each notification information to an array of notifications with append.
				This is the main format I return lists of stuff in since GSON can pull that information
				and put it into an array of a certain object.
				'''
				notifications.append(notification.JSONOutputShort())
		'''
		json.dumps is the way to write json to the response. This is because it takes json and returns a string of json.
		There is a json.dump function but that returns an actual JSON object and does not write correctly.
		Also, notice that there are {} in the .dumps function. This is the other list structure in python, dictionary.
		It is similar to map in java. You give it a string as a key and some other variable as the value
		'''
		self.response.write(json.dumps({'statusCode': statusCode, 'notifications': notifications}))

	def getDetail(self, notificationId):
		#statusCodes:
		#200 ok
		#201 other error
		statusCode = 201
		notifications = []
		if user:
			statusCode = 200
			notification = Notification.get_by_id(notificationId)
			notifications.append(notification.JSONOutputDetail())
		self.response.write(json.dumpgs({'statusCode': statusCode, 'notifications': notifications}))

	def post(self):
		statusCode = 200
		recurrenceRate = self.request.get('recurrenceRate')
		senderEmail = self.request.get('senderEmail')
		receiverEmail = self.request.get('receiverEmail')
		sender = User.query(User.emailAddress == senderEmail).get()
		if sender:
			receiver = User.query(User.emailAddress == receiverEmail).get()
			if receiver:
				'''
				A lot of this is a repeat of above. One thing that is different is I look for both receiverId and receiverAddress.
				This is because I am not sure what the user will send to the API. Basically, I look for receiverId first, if it
				does exist I move on, if not, then I look for the receiverAddress, use that to get the user, and then set the Id.
				It just adds a little bit of flexibility for the user.

				below is an example of using the request body to populate contents. In this we use json.loads. It is the reverse
				of json.dumps, it takes a string and returns a json object (really just a map with stuff in it). Now that I am
				looking at it, I am not sure if I need to loads and then dumps. I think it is because self.request.body is not
				written in actual characters so we need to load and then dump into an actual string object but I haven't tested
				enough to know either way. But I know this works.
				'''
				contents = json.dumps(json.loads(self.request.body))
				'''
				This is how you create a new entity row. Use the constructor and pass whatever variables you want to it. 
				There are some variables that are auto filled (like creationDate) these should not be given a value. There
				are attributes that have a default value, these can be given values but is not required, if a value is not given
				it uses the default value. Everything else needs a value or it is null. The constructor returns a key to the newly
				created object but it has not been added to the table yet. You need to call .put() to add it. This is also how you
				update rows. Call notification.query().get() to get the row, make any changes, and then call .put() to update the table.

				'''
				notification = Notification(contents=contents, recurrenceRate=recurrenceRate, senderEmail=senderEmail, receiverEmail=receiverEmail)
				notification.put()
		self.response.write(json.dumps({'statusCode': statusCode}))

	def delete(self):
		'''
		This is a very simple delete function that uses notificationId to select the notification and then delete it.
		Although, you need to do .key.delete() in order to delete it. There is no delete function for entities. In fact
		there is no put function either, the constructor above returns a key. So you actually need to call .key.put()
		'''
		statusCode = 201
		notificationId = self.request.get('notificationId')
		logging.info(self.request.get('notificationId'))
		notification = Notification.get_by_id(int(notificationId))
		if notification:
			statusCode = 200
			notification.key.delete()
		logging.info(statusCode)
		self.response.write(json.dumps({'statusCode': statusCode}))

class ExerciseHandler(webapp2.RequestHandler):
	def get(self):
		statusCode = 200
		exercises = []
		for exercise in Exercise.query():
			exercises.append(exercise.JSONOutput())
		self.response.write(json.dumps({'statusCode': statusCode, 'exercises': exercises}))

	def post(self):
		statusCode = 200
		emailAddress = self.request.get('emailAddress')
		logging.info(self.request.body)
		logging.info(json.loads(self.request.body))
		exerciseContents = json.loads(self.request.body)
		name = exerciseContents['name']
		notes = exerciseContents['notes']
		multimedia = exerciseContents['multimedia']
		description = exerciseContents['description']
		tags = exerciseContents['tags']
		exercise = Exercise(name=name, notes=notes, multimedia=multimedia, description=description, tags=tags, createdBy=emailAddress)
		exercise.put()
		self.response.write(json.dumps({'statusCode': statusCode}))

class WorkoutHandler(webapp2.RequestHandler):
	def get(self):
		statusCode = 201
		workoutId = self.request.get('workoutId')
		if workoutId != '':
			workout = Workout.get_by_id(workoutId)
			if workout:
				statusCode = 200
				self.response.write(json.dumps({'statusCode': statusCode, 'workout': workout.JSONOutput()}))
			else:
				self.response.write(json.dumps({'statusCode': statusCode}))
		else:
			statusCode = 200
			workoutName = self.request.get('workoutNames')
			workouts = []
			for workout in Workout.query():
				workouts.append(workout.JSONOutput())
			self.response.write(json.dumps({'statusCode': statusCode, 'workouts': workouts}))

	def post(self):
		statusCode = 202
		emailAddress = self.request.get('emailAddress')
		workoutContents = json.loads(self.request.body)
		user = User.query(User.emailAddress==emailAddress).get()
		if user:
			statusCode = 201
			createdBy = emailAddress
			name = workoutContents['name']
			description = workoutContents['description']
			tags = workoutContents['tags']
			rating = 5
			exercises = workoutContents['exercises']
			workout = Workout(createdBy=createdBy,
				name=name,
				description=description,
				tags=tags,
				rating=rating,
				exercises=json.dumps(exercises))
			workout.put()
			statusCode = 200
		self.response.write(json.dumps({'statusCode': statusCode}))

'''
class WeightHandler(webapp2.RequestHandler):
	def get(self):
		username = str(self.request.get('username'))
		dateSent = True
		try:
			date = str(self.request.get('date'))
		except KeyError:
			dateSent = False
		all_weight = []
		if dateSent:
			for weight in itWeight.query(itWeight.username == username):
				all_weight.append(weight)
			all_weight = sorted(all_weight,key=lambda r: r.date,reverse=True)
		else:
			str s = ""
		return_list = []
		for weight in all_weight:
			return_list.append({'weight': weight.weight, 'date': weight.date})
		self.response.write(json.dumps(return_list))

	def post(self):
		statusCode = 202
		username = str(self.request.get('username'))
		weight = float(self.request.get('weight'))

		newWeight = itWeight(weight = weight, username = username)
		if newWeight:
			newWeight.put()
			statusCode = 200

		self.response.write(json.dumps({'statusCode': statusCode}))
'''
application = webapp2.WSGIApplication([
    ('/login', LoginHandler),
    ('/register', RegisterHandler),
	('/trainer', TrainerHandler),
	('/notification', NotificationHandler),
	('/exercise', ExerciseHandler),
	('/workout', WorkoutHandler)
], debug=True)
