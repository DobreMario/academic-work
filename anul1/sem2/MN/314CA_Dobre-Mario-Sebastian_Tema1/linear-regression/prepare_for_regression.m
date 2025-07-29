function [FeatureMatrix] = prepare_for_regression(InitialMatrix)
  	% InitialMatrix -> the matrix that must be transformed

  	% FeatureMatrix -> the matrix with all training examples
  
  	% TODO: prepare_for_regression implementation

	[m, n] = size(InitialMatrix);
	FeatureMatrix = zeros(m, n + 1);

	for i = 1 : m
		k = 1;
		for j = 1 : n
			x = ~isnan(str2double(InitialMatrix{i, j}));
			if x == 1
				FeatureMatrix(i, k) = str2double(InitialMatrix{i, j});
			else
				s = InitialMatrix{i, j};
				if strcmp(s, 'yes')
					FeatureMatrix(i, k) = 1;
				elseif strcmp(s, 'no')
					FeatureMatrix(i, k) = 0;
				else
					if strncmp(s, 'semi-', 5)
						FeatureMatrix(i, k : k + 1) = [1, 0];
						k = k + 1;
					elseif strncmp(s, 'un', 2)
						FeatureMatrix(i, k : k + 1) = [0, 1];
						k = k + 1;
					else
						FeatureMatrix(i, k : k + 1) = [0, 0];
						k = k + 1;
					end
				end
			end
			k = k + 1;
		end
	end

end
