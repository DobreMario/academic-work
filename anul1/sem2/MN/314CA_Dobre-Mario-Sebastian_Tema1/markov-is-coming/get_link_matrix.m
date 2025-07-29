function [Link] = get_link_matrix (Labyrinth)
	% Labyrinth -> the matrix of all encodings

	% Link -> the link matrix associated to the given labyrinth

	% TODO: get_link_matrix implementation
	
	Adj = full(get_adjacency_matrix(Labyrinth));

	tmp = sum(Adj, 2);
	% sum(matrix, 2) will calculate the row`s sums
	Link_Supp = Adj ./ tmp;
	Link = sparse(Link_Supp);
end
