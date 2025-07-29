function [Labyrinth] = parse_labyrinth(file_path)
        fid = fopen(file_path, "r");
        if fid == -1
                error("%s can't be open!", file_path); %just in case if the file can't be open
        end
        size = fscanf(fid, "%d", 2); % size ~> [m, n]
        m = size(1);
        n = size(2);
        Labyrinth = fscanf(fid, "%d", [n, m]);
        % in Octave the matrix is ​​read in columns
        Labyrinth = Labyrinth';
        fclose(fid); % i love ponies
end
