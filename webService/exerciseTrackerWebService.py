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
		userId = User.generateId()
		emailCheck = User.query(User.emailAddress == str(emailAddress)).get()
		if emailCheck:
			statusCode = 201
		else:
			newUser = User(userId = userId, name = name, emailAddress = emailAddress, permissionFlag = permissions)
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
			if getTrainer == 1:
				trainers = []
				for trainerId in Trains.query(Trains.traineeId == user.userId):
					trainer = User.query(User.userId == trainerId.trainerId).get()
					if trainer:
						trainers.append(trainer.getViewableInfo())
						statusCode = 200
						self.response.write(json.dumps({'statusCode': statusCode, 'trainers': trainers}))
			else:
				trainees = []
				for traineeId in Trains.query(Trains.trainerId == user.userId):
					trainee = User.query(User.userId == traineeId.traineeId).get()
					if trainee:
						trainee.append(trainee.getViewableInfo())
						statusCode = 200
						self.response.write(json.dumps({'statusCode': statusCode, 'trainees': trainees}))

	def post(self):
		#statusCodes:
		#200 ok
		#201 invalid other address
		#202 invalid user address
		#203 other error
		statusCode = 203
		addTrainer = self.request.get('addTrainer')
		emailAddress = self.request.get('emailAddress')
		otherAddress = self.request.get('otherAddress')
		statusCode = 202
		user = User.query(User.emailAddress == emailAddress).get()
		if user:
			statusCode = 201
			other = User.query(User.emailAddress == otherAddress).get()
			if other:
				if addTrainer == 1:
					train = Trains(other.userId, user.userId)
					train.put()
				else:
					train = Trains(user.userId, other.userId)
					train.put()
				statusCode = 200
		self.response.write(json.dumps({'statusCode': statusCode}))

class NotificationHandler(webapp2.RequestHandler):
	def get(self):
		email = str(self.request.get('emailAddress'))
		user = User.query(User.emailAddress == email).get()
		notificationId = self.request.get('notificationId')
		if notificationId == '':
			self.getShort(email, user)
		else:
			self.getDetail(notificationId)

	def getShort(self, email, user):
		#statusCodes:
		#200 ok
		#201 other error
		statusCode = 201
		notifications = []
		if user:
			statusCode = 200
			for notification in Notification.query(Notification.receiverId == user.userId):
				notifications.append(notification.JSONOutputShort())
		self.response.write(json.dumps({'statusCode': statusCode, 'notifications': notifications}))

	def getDetail(self, notificationId):
		#statusCodes:
		#200 ok
		#201 other error
		statusCode = 201
		notifications = []
		if user:
			statusCode = 200
			notification = Notification.query(Notification.notificationId == notificationId).get()
			notifications.append(notification.JSONOutputDetail())
		self.response.write(json.dumpgs({'statusCode': statusCode, 'notifications': notifications}))

	def post(self):
		statusCode = 200
		recurrenceRate = self.request.get('recurrenceRate')
		senderId = self.request.get('senderId')
		if senderId == '':
			senderEmail = self.request.get('senderAddress')
			sender = User.query(User.emailAddress == senderEmail).get()
			if sender:
				senderId = sender.userId
		receiverId = self.request.get('receiverId')
		if receiverId == '':
			receiverEmail = self.request.get('receiverAddress')
			receiver = User.query(User.emailAddress == receiverEmail).get()
			if receiver:
				receiverId = receiver.userId
		contents = json.dumps(json.loads(self.request.body))
		notificationId = int(Notification.generateId())
		notification = Notification(notificationId=notificationId, contents=contents, recurrenceRate=recurrenceRate, senderId=int(senderId), receiverId=int(receiverId))
		notification.put()
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
		logging.info(json.loads(self.request.body))
		exerciseContents = json.loads(self.request.body)
		exerciseId = Exercise.generateId()
		name = exerciseContents['name']
		notes = exerciseContents['notes']
		multimedia = exerciseContents['multimedia']
		description = exerciseContents['description']
		tags = exerciseContents['tags']
		user = User.query(User.emailAddress==emailAddress).get()
		exercise = Exercise(exerciseId=exerciseId, name=name, notes=notes, multimedia=multimedia, description=description, tags=tags, createdBy=user.userId)
		exercise.put()
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
	('/exercise', ExerciseHandler)
], debug=True)
