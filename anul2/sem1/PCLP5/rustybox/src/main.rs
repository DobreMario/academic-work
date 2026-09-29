use std::{fs::{self, read_to_string}, path::{Path, PathBuf}, process::exit};

fn rs_pwd() {
    let curr_dir = std::env::current_dir().unwrap();
    println!("{}", curr_dir.display());
}

fn rs_echo(args : &[String]) {

    let mut new_line: bool = true;
    for arg in args {
        if arg.starts_with("-") {
            if arg != "-n" {
                println!("Invalid command!");
                exit(-10);
            } else {
                new_line = false;
            }
        } else {
            print!("{} ", arg);
        }
    }

    if new_line {
        println!("");
    }

    exit(0);
}

fn rs_cat(args: &[String]) {
    let mut exit_code = 0;
    for file_name in args {
        let path = Path::new(file_name);
        if path.exists() {
            if let Ok(line) = read_to_string(file_name) {
                println!("{}", line);
            } else {
                println!("cat: {}: Is a directory", file_name);
                exit_code = -20;
            }
        } else {
            println!("cat: {}: No such file or directory", file_name);
            exit_code = -20;
        }
    }

    exit(exit_code);
}

fn rs_mkdir(args : &[String]) {
    let mut exit_code = 0;

    for dir_name in args {
        let path = Path::new(dir_name);
        let check = fs::create_dir(path);
        if let Err(e) = check {
            println!("mkdir: cannot create directory ‘{}’: {}", dir_name, e);
            exit_code = -30;
        }

    }

    exit(exit_code);
}

fn rs_mv(args : &[String]) {
    if args.len() < 2 {
        println!("{}", args.len());
        eprintln!("Usage: mv <source1> [source2 ...] <destination>");
        exit(-30);
    }

    let mut exit_code = 0;
    let len = args.len();
    let dest = args.get(len - 1).unwrap();
    let dest_path = Path::new(dest);

     if len > 2 && !dest_path.is_dir() {
        eprintln!("mv: target {}: Not a directory", dest);
        exit(1);
    }

    for src in &args[0..len - 1] {
        let src_path = Path::new(src);

        if !src_path.exists() {
            eprintln!("mv: cannot stat {}: No such file or directory", src);
            exit_code = -30;
            continue;
        }

        let mut final_path = PathBuf::from(dest_path);
        if dest_path.is_dir() {
            final_path.push(src_path.file_name().unwrap());
        }

        if let Err(e) = fs::rename(src_path, &final_path) {
            eprintln!("mv: cannot move {}: {}", src, e);
            exit_code = -30;
        }
        
    }

    exit(exit_code);
}

fn rs_ln() {
    println!("Will implement ln command.");
}

fn rs_rmdir() {
    println!("Will implement rmdir command.");
}

fn rs_rm() {
    println!("Will implement rm command.");
}

fn rs_ls(args : &[String]) {
    if args.len() == 0 {
        return;
    }

    let dir_path = Path::new(args.get(0).unwrap());
    let entities = fs::read_dir(dir_path).unwrap();
    for each in entities {
        let entry = each.unwrap();
        let path = entry.path();
        println!("{:?}", path.file_name().unwrap());
    }
}

fn rs_cp() {
    println!("Will implement cp command.");
}

fn rs_touch() {
    println!("Will implement touch command.");
}

fn rs_chmod() {
    println!("Will implement chmod command.");
}

fn find_in_dictionary(arg: &str) -> String {
    match arg {
        "--recursive" => "-r".to_string(),
        "--force"     => "-f".to_string(),
        "--verbose"   => "-v".to_string(),
        other   => other.to_string(),
    }
}


fn my_unwrap(args: &[String]) -> (String, Vec<String>){
    let mut option = String::new();
    let mut rest = Vec::new();
    for arg in args {
        let tmp = find_in_dictionary(arg);
        if tmp.len() == 2 && tmp.starts_with("-") {
            option.push(tmp.chars().nth(1).unwrap());
        } else {
            rest.push(tmp);
        }
    }
    return (option, rest);
}

fn main() {
    let args: Vec<String> = std::env::args().collect();
    if args.len() <= 1 {
        println!("No command provided.");
        exit(-1);
    } else {
        let arg = args.get(1);
        let (str1, str2) = my_unwrap(&args[2..]);
        println!("{} ~ {:?}", str1, str2);
        let command = arg.unwrap();
            match command.as_str() {
                "pwd" => rs_pwd(),
                "echo" => rs_echo(&args[2..]),
                "cat" => rs_cat(&args[2..]),
                "mkdir" => rs_mkdir(&args[2..]),
                "mv" => rs_mv(&args[2..]),
                "ln" => rs_ln(),
                "rmdir" => rs_rmdir(),
                "rm" => rs_rm(),
                "ls" => rs_ls(&args[2..]),
                "cp" => rs_cp(),
                "touch" => rs_touch(),
                "chmod" => rs_chmod(),
                _ => println!("Invalid command: {}", command),
            }
        }
}