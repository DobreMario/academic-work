function [decoded_path] = decode_path (path, lines, cols)
	% path -> vector containing the order of the states that arrived
	% 		 to a winning position
	% lines -> numeber of lines
	% cols -> number of columns
	
	% decoded_path -> vector of pairs (line_index, column_index)
	%                 corresponding to path states
	% hint: decoded_path does not contain indices for the WIN state

	% TODO: decode_path implementation

	n = length(path) - 1; % length without WIN state
	decoded_path = zeros(n, 2);
	row = zeros(n, 1);
	col = zeros(n, 1);
	[col(:), row(:)] = ind2sub([cols, lines], path(1 : n));
	decoded_path(1 : end, :) = [row, col];

	% NOTE:
	%		ind2sub is a elegant option for :
	%										  row = mod(idx - 1, m) + 1;
	%										  col = floor((idx - 1) / m) + 1;
	%		And it work on matrixs like this:
	%											1 4 7
	%											2 5 8
	%											3 6 9

end
