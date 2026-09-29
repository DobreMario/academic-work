use std::{collections::HashMap, io, rc::Rc};

fn read_string(promt: &str) -> String {
    println!("{}", promt);
    let mut input = String::new();
    io::stdin().read_line(&mut input).unwrap();
    input.trim().to_string()
}

fn read_number(promt: &str) -> i32 {
    println!("{}", promt);
    let mut input = String::new();
    io::stdin().read_line(&mut input).unwrap();
    input.trim().parse().unwrap()
}

struct File {
    name: String,
    extensie: String,
    marime: i32,
}

struct PlanEntry {
    file: Rc<File>,
    rule: String,
}

impl File {
    fn print(&self) {
        println!("Nume fisier: {}", self.name);
        println!("Dimnesiune fisier: {}", self.marime);
        println!("Extensie fisier: {}", self.extensie);
    }
}

impl PlanEntry {
    fn print(&self) {
        println!("Nume fisier: {}", self.file.name);
        println!("Dimnesiune fisier: {}", self.file.marime);
        println!("Extensie fisier: {}", self.file.extensie);
        println!("Regula aplicata: {}", self.rule);
    }
}

fn add(files: &mut Vec<Rc<File>>) {
    let nume: String = read_string("Alege nume: ");
    let extensie: String = read_string("Alege extensie: ");
    let nr: i32 = read_number("Alege dimensiune: ");
    let file: File = File { name: nume, extensie: extensie, marime: nr};
    files.push(Rc::new(file));
}

fn regula(regula: &mut HashMap<String, String>) {
    let rule: String = read_string("Citeste regula");
    let extensie: String = read_string("Citeste extesia");
    regula.insert(extensie, rule);
}

fn show_plan(files: &mut Vec<Rc<File>>, rules: &mut HashMap<String, String>) {
    for file in files {
        let extensie: String = file.extensie.clone();
        let rule = rules.get(&extensie);
        match rule {
            None => println!("Extensia: {}, nu are regula:(", extensie),
            Some(s) => println!("Extensia: {}, are regula: {}", extensie, s),
        }
    }
}

fn new_plan(plan: &mut Vec<PlanEntry>,
           files: &Vec<Rc<File>>,
           rules: &HashMap<String, String>) {

    for file in files {
        if let Some(rule_string) = rules.get(&file.extensie) {
            let entry = PlanEntry {
                file: Rc::clone(file),
                rule: rule_string.clone(),
            };
            plan.push(entry);
        }
    }
}

fn total_space(files: &mut Vec<Rc<File>>) {
    let mut sum: i32 = 0;
    for file in files {
        sum += file.marime;
    }

    println!("Spatiul pe 'folder' este: {}", sum);
}

fn top(files: &Vec<Rc<File>>) {
    let mut max0: i32 = -1;
    let mut max1: i32 = -1;
    let mut max2: i32 = -1;
    for file in files {
        if file.marime > max0 {
            max2 = max1;
            max1 = max0;
            max0 = file.marime;
        } else if file.marime > max1 {
            max2 = max1;
            max1 = file.marime;
        } else if file.marime > max2 {
            max2 = file.marime;
        }
    }

    for file in files {
        if file.marime == max0 {
            file.print();
        }

        if file.marime == max1 {
            file.print();
        }

        if file.marime == max2 {
            file.print();
        }
    }
}


fn main() {
    println!("Simulator fisiere");

    let mut file_system: Vec<Rc<File>> = Vec::new();
    let mut rule: HashMap<String, String> = HashMap::new();
    let mut plan: Vec<PlanEntry> = Vec::new();

    loop {
        println!("Comnezi");
        println!("1: Adauga fisier");
        println!("2: Defineste regula");
        println!("3: Vezi plan");
        println!("4: Aplica planul");
        println!("5: Vezi planul aplicat");
        println!("6: Vezi spatiul");
        println!("7: Cele mai mari 3 fisere");
        println!("0: Exit");

        let id: i32 = read_number("Alege:");
        match id {
            1 => add(&mut file_system),
            2 => regula(&mut rule),
            3 => show_plan(&mut file_system, &mut rule),
            4 => new_plan(&mut plan, &file_system, &rule),
            5 => {
                if plan.len() == 0 {
                    println!("Planul nu a fost aplicat");
                } else {
                    for entry in &plan {
                        entry.print();
                    }
                }
            }
            6 => total_space(&mut file_system),
            7 => top(&file_system),
            0 => break,
            _ => print!("Comanda invalida"),
        };
    }
}
