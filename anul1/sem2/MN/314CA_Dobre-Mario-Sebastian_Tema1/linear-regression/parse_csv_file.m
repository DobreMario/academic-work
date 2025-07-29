function [Y, InitialMatrix] = parse_csv_file(file_path)
  	% path -> a relative path to the .csv file
  
  	% Y -> the vector with all actual values
  	% InitialMatrix -> the matrix that must be transformed

  	% TODO: parse_csv_file implementation

	fid = fopen(file_path, 'r');
	if fid == -1
		error("%s can't be open!", file_path);
	end

	headerLine = fgetl(fid);
	format = '%d%f%f%f%f%s%s%s%s%s%f%s%s';

	DataMatrix = textscan(fid, format, 'Delimiter', ',');

	Y = DataMatrix{1};
	cols = numel(DataMatrix) - 1;
	InitialMatrix = cell(length(Y), cols);

	for i = 2 : numel(DataMatrix)
		if iscell(DataMatrix{i})
			InitialMatrix(:, i - 1) = DataMatrix{i};
			% If DataMatrix{i} is a cell array (containing strings), 
			% we directly assign it to InitialMatrix since the data is already 
			% in the correct format (a cell array of strings).
		else
			InitialMatrix(:, i - 1) = num2cell(DataMatrix{i});
		end
	end


	fclose(fid);
end
