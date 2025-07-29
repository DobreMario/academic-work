function [G, c] = get_Jacobi_parameters (Link)
	% Link -> the link matrix (edge with probabilities matrix)
	Link = full(Link);
	[n, m] = size(Link);
	% G -> iteration matrix
	G = Link(1 : n - 2, 1 : m - 2);

	% c -> iteration vector
	c = Link(1 : n - 2, m - 1);
	% TODO: get_Jacobi_parameters

end
