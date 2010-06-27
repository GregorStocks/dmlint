import re
import os.path
import sys

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
		if filename.find('.dmp') != -1 or filename.find('dmf') != -1:
			# ignore #includes of maps, at least for now
			return
		included.append(filename)

		# For now, don't worry about sequential yadda yadda - just read the
		# entire file instantly.
		with file(filename, 'rb') as f:
			for line in f.readlines():
				self.data += process_line(line, defines, included,
						os.path.split(filename)[0])
		# TODO: actually handle text documents the right way
		self.data = re.sub('(?s){"(.+?)"}', '""', self.data)
		self.data = re.sub('\\r', '', self.data)

		# convert all instances of "xxx[yyy]zzz" to text("xxx[]zzz", yyy)
		r = '''(?x)
		"
		(?P<left>(.*? [^\\\\]) | )
		\[
		(?P<middle>[^\]]*? [^\]\\\\])
		\]
		(?P<right>.*?)"'''
		while re.search(r, self.data):
			self.data = re.sub(r, lambda m: 'text("%s[]%s", %s)' % m.group('left', 'right', 'middle'), self.data)

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
	m = re.match('#define\s+(?P<from>\w+)(\s+(?P<to>.*?))?\s*(//.*)?$', line)
	if m is not None:
		defines.insert(0, m.group('from', 'to'))
		return ''

	# if it's an #include, include it!
	m = re.match('#include "(?P<filename>.+)"', line)
	if m is not None:
		return PreprocessedFile(os.path.join(dir, m.group('filename')), defines,
				included).read() + '\n'
	# TODO: #undef, __FILE__, #if, and all those other ones
	# but they're lame so yknow
	if line.strip().startswith('#'):
		return '\n'
	# Process all the #defines until none are applicable any more.
	# This probably isn't how a preprocessor is supposed to work, but
	# who cares?
	for fr, to in defines:
		if line.find(fr) != -1:
			return process_line(line.replace(fr, to, 1), defines, included, dir)
	
	return line

