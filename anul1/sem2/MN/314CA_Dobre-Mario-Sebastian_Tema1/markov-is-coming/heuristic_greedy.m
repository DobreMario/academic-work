function [path] = heuristic_greedy (start_position, probabilities, Adj)
	% start_position -> the starting point in the labyrinth
	% probabilities -> vector of associated probabilities: including WIN and LOSE
	% Adj -> adjacency matrix
	
	% path -> the states chosen by the algorithm
  
	% TODO: heuristic_greedy implementation
	Adj = full(Adj);
	path = [start_position];
	visited = false(1, length(probabilities));
	visited(start_position) = true;
	while ~isempty(path)
		position = path(end); % top of path 
		if probabilities(position) == 1
			return;
		end
		neighbours = find(Adj(position, :) == 1);
		un_neighbours = neighbours(~visited(neighbours));

		if isempty(un_neighbours)
			path(end) = [];
		else
			[~, idx] = max(probabilities(un_neighbours));
			next_neighbour = un_neighbours(idx);
			visited(next_neighbour) = true;
			path = [path, next_neighbour];
		end
	end
	return;
end
