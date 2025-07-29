function [Adj] = get_adjacency_matrix(Labyrinth)
    % Labyrinth -> the matrix of all encodings
    % Adj -> the adjacency matrix associated to the given labyrinth
    % TODO: get_adjacency_matrix implementation
    [n, m] = size(Labyrinth);
    Adj_supp = zeros(n + 2, m + 2);

    for i = 1 : n
        for j = 1 : m
            cod = dec2bin(Labyrinth(i, j), 4); % Convert the number to a 4-bit binary string
            idx = (i - 1) * m + j;

            % Check if there's no wall to the north (bit 1)
            if cod(1) == '0'
                if i == 1
                    Adj_supp(idx, n * m + 1) = 1;  % Add connection to index n * m + 1
                else
                    Adj_supp(idx, (i - 2) * m + j) = 1;
                end
            end

            % Check if there's no wall to the south (bit 2)
            if cod(2) == '0'
                if i == n
                    Adj_supp(idx, n * m + 1) = 1;  % Add connection to index n * m + 1
                else
                    Adj_supp(idx, i * m + j) = 1;
                end
            end

            % Check if there's no wall to the east (bit 3)
            if cod(3) == '0'
                if j == m
                    Adj_supp(idx, n * m + 2) = 1;  % Add connection to index n * m + 2
                else
                    Adj_supp(idx, (i - 1)* m + j + 1) = 1;
                end
            end

            % Check if there's no wall to the west (bit 4)
            if cod(4) == '0'
                if j == 1
                    Adj_supp(idx, n * m + 2) = 1;  % Add connection to index n * m + 2
                else
                    Adj_supp(idx, (i - 1) * m + j - 1) = 1;
                end
            end
        end
    end

    Adj_supp(n * m + 1, n * m + 1) = 1;
    Adj_supp(n * m + 2, n * m + 2) = 1;
	Adj = sparse(Adj_supp);

end
