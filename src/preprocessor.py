import re
import os.path

'''A file which has had the preprocessor run on it.'''
class PreprocessedFile(object):
	data = None
	curbyte = 0
	def __init__(self, filename, defines = None, included = None):
		if defines is None:
			defines = [('DM_VERSION', '300')] # Good enough.
		if included is None:
			included = []
		self.data = ''

		filename = os.path.abspath(filename)
		if filename in included:
			return
		included.append(filename)

		# For now, don't worry about sequential yadda yadda - just read the
		# entire file instantly.
		with file(filename, 'rb') as f:
			for line in f.readlines():
				self.data += process_line(line, defines, included,
						os.path.split(filename)[0]) + '\n'

	def read(self, numbytes = -1):
		if numbytes >= len(self.data) or numbytes < 0:
			r = self.data
			self.data = ''
			return r
		else:
			r = self.data[:numbytes]
			self.data = self.data[numbytes:]
			return r
		# Line numbers are doomed. TODO: fix that

'''The result of preprocessing the given line with the given set of defines and
files already included and in the given directory.'''
def process_line(line, defines, included, dir):
	# if it's a #define, add it to the defines list
	m = re.match('#define\s+(?P<from>\w+)(\s+(?P<to>.*?))?$', line)
	if m is not None:
		defines.append(m.group('from', 'to'))
		return ''

	# if it's an #include, include it!
	m = re.match('#include "(?P<filename>.+)"', line)
	if m is not None:
		f = PreprocessedFile(os.path.join(dir, m.group('filename')), defines,
				included)
		return f.read()
	# TODO: #undef, __FILE__, #if, and all those other ones
	# but they're lame so yknow
	# Process all the #defines until none are applicable any more.
	# This probably isn't how a preprocessor is supposed to work, but
	# who cares?
	for fr, to in defines:
		if line.find(fr) != -1:
			return process_line(line.replace(fr, to, 1), defines, included, dir)
	
	return line
