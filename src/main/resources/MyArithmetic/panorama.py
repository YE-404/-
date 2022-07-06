# import the necessary packages
import numpy as np
import imutils
import cv2

class Stitcher:
	def __init__(self, vertical=True):
		# determine if we are using OpenCV v3.X
		#self.isv3 = imutils.is_cv3()
		#print('self.isv3 = ',self.isv3)
		self.isv3 = True
		self.isVertical = vertical

	def stitch(self, images, ratio=0.75, reprojThresh=4.0,
		showMatches=False, vertical=True):
		# unpack the images, then detect keypoints and extract
		# local invariant descriptors from them

		(imageB, imageA) = images
		(kpsA, featuresA) = self.detectAndDescribe(imageA)
		(kpsB, featuresB) = self.detectAndDescribe(imageB)

		# match features between the two images
		M = self.matchKeypoints(kpsA, kpsB,
			featuresA, featuresB, ratio, reprojThresh)

		# if the match is None, then there aren't enough matched
		# keypoints to create a panorama
		if M is None:
			return None

		# otherwise, apply a perspective warp to stitch the images
		# together
		(matches, H, status) = M



		if self.isVertical == True:
			result = cv2.warpPerspective(imageA, H, (imageA.shape[1], imageA.shape[0] + imageB.shape[0]))
			result[0:imageB.shape[0], 0:imageB.shape[1]] = imageB

		else:
			result = cv2.warpPerspective(imageA, H, (imageA.shape[1] + imageB.shape[1], imageA.shape[0]))
			result[0:imageB.shape[0], 0:imageB.shape[1]] = imageB

		# check to see if the keypoint matches should be visualized
		# if showMatches:
		# 	vis = self.drawMatches(imageA, imageB, kpsA, kpsB, matches,
		# 		status)
		# 
		# 	# return a tuple of the stitched image and the
		# 	# visualization
		# 	return (result, vis)

		# return the stitched image
		return result

	def detectAndDescribe(self, image):
		# convert the image to grayscale
		gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)

		# check to see if we are using OpenCV 3.X
		if self.isv3:
			# detect and extract features from the image
			#descriptor = cv2.xfeatures2d.SIFT_create()
			##descriptor = cv2.SIFT()
			descriptor = cv2.SIFT_create()
			(kps, features) = descriptor.detectAndCompute(image, None)

		# otherwise, we are using OpenCV 2.4.X
		else:
			# detect keypoints in the image
			detector = cv2.FeatureDetector_create("SIFT")
			kps = detector.detect(gray)

			# extract features from the image
			extractor = cv2.DescriptorExtractor_create("SIFT")
			(kps, features) = extractor.compute(gray, kps)

		# convert the keypoints from KeyPoint objects to NumPy
		# arrays
		kps = np.float32([kp.pt for kp in kps])

		# return a tuple of keypoints and features
		return (kps, features)

	def matchKeypoints(self, kpsA, kpsB, featuresA, featuresB,
		ratio, reprojThresh):
		# compute the raw matches and initialize the list of actual
		# matches
		matcher = cv2.DescriptorMatcher_create("BruteForce")
		rawMatches = matcher.knnMatch(featuresA, featuresB, 2)
		matches = []

		# loop over the raw matches
		for m in rawMatches:
			# ensure the distance is within a certain ratio of each
			# other (i.e. Lowe's ratio test)
			if len(m) == 2 and m[0].distance < m[1].distance * ratio:
				matches.append((m[0].trainIdx, m[0].queryIdx))

		# computing a homography requires at least 4 matches
		if len(matches) > 4:
			# construct the two sets of points
			ptsA = np.float32([kpsA[i] for (_, i) in matches])
			ptsB = np.float32([kpsB[i] for (i, _) in matches])

			# compute the homography between the two sets of points
			(H, status) = cv2.findHomography(ptsA, ptsB, cv2.RANSAC,
				reprojThresh)

			# return the matches along with the homograpy matrix
			# and status of each matched point
			return (matches, H, status)

		# otherwise, no homograpy could be computed
		return None

	def drawMatches(self, imageA, imageB, kpsA, kpsB, matches, status):
		# initialize the output visualization image
		(hA, wA) = imageA.shape[:2]
		(hB, wB) = imageB.shape[:2]

		if self.isVertical == True:
		# 	vis = np.zeros((hA + hB, max(wA, wB), 3), dtype="uint8")
		# 	vis[0:hA, 0:wA] = imageA
		# 	vis[hA:, 0:wB] = imageB
		# else:
			vis = np.zeros((max(hA, hB), wA + wB, 3), dtype="uint8")
			vis[0:hA, 0:wA] = imageA
			vis[0:hB, wA:] = imageB

		# loop over the matches
		for ((trainIdx, queryIdx), s) in zip(matches, status):
			# only process the match if the keypoint was successfully
			# matched
			if s == 1:
				# draw the match
				ptA = (int(kpsA[queryIdx][0]), int(kpsA[queryIdx][1]))
				ptB = (int(kpsB[trainIdx][0]) + wA, int(kpsB[trainIdx][1]))
				cv2.line(vis, ptA, ptB, (0, 255, 0), 1)

		# return the visualization
		return vis


def remove_space(image):
	img = cv2.medianBlur(image, 5)  # 中值滤波，去除黑色边际中可能含有的噪声干扰
	b = cv2.threshold(img, 15, 255, cv2.THRESH_BINARY)  # 调整裁剪效果
	binary_image = b[1]  # 二值图--具有三通道
	binary_image = cv2.cvtColor(binary_image, cv2.COLOR_BGR2GRAY)
	print(binary_image.shape)  # 改为单通道

	x = binary_image.shape[0]
	# print("高度x=", x)
	y = binary_image.shape[1]
	# print("宽度y=", y)
	edges_x = []
	edges_y = []
	for i in range(x):
		for j in range(y):
			if binary_image[i][j] == 255:
				edges_x.append(i)
				edges_y.append(j)

	left = min(edges_x)  # 左边界
	right = max(edges_x)  # 右边界
	width = right - left  # 宽度
	bottom = min(edges_y)  # 底部
	top = max(edges_y)  # 顶部
	height = top - bottom  # 高度

	pre1_picture = image[left:left + width, bottom:bottom + height]  # 图片截取
	return pre1_picture  # 返回图片数据


