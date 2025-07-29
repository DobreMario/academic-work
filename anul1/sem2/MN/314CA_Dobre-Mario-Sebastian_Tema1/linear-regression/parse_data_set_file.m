function [Y, InitialMatrix] = parse_data_set_file(file_path)
  	% path -> a relative path to the .txt file

  	%	Y -> the vector with all actual values
  	% InitialMatrix -> the matrix that must be transformed
  
  	% TODO: parse_data_set_file implementation
  	fid = fopen(file_path, 'r');
	if fid == -1
		error("%s can't be open!", file_path);
	end

	size = fscanf(fid, "%d", 2);
	m = size(1);
	n = size(2);

	DataMatrix = cell(m, n + 1);

	InitialMatrix = cell(n, m);

	for i = 1 : m
		for j = 1 : n + 1
			DataMatrix{i, j} = fscanf(fid, "%s", 1);
		end
	end
	DataMatrix = DataMatrix';
	Y = str2double(DataMatrix(1, :));
	InitialMatrix(1 : end, 1 : end) = DataMatrix(2 : end, 1 : end);
	InitialMatrix = InitialMatrix';

	fclose(fid);
end
