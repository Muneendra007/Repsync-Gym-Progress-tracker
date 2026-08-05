import fs from 'fs';
import path from 'path';

const srcDir = './src';

function walk(dir) {
  let results = [];
  const list = fs.readdirSync(dir);
  list.forEach((file) => {
    file = path.join(dir, file);
    const stat = fs.statSync(file);
    if (stat && stat.isDirectory()) {
      results = results.concat(walk(file));
    } else {
      if (file.endsWith('.jsx') || file.endsWith('.js') || file.endsWith('.css')) {
        results.push(file);
      }
    }
  });
  return results;
}

const files = walk(srcDir);

files.forEach(file => {
  let content = fs.readFileSync(file, 'utf8');
  const original = content;

  // Replacements
  // "REPSYNC AI Gym" -> "REPSYNC Gym"
  content = content.replace(/REPSYNC AI Gym/g, 'REPSYNC Gym');
  // "REPSYNC AI" -> "REPSYNC"
  content = content.replace(/REPSYNC AI/g, 'REPSYNC');
  // " AI " -> " "
  content = content.replace(/ AI /g, ' ');
  // "AI " -> "" (at start of string or after quote/bracket)
  content = content.replace(/>AI /g, '>');
  content = content.replace(/> AI /g, '>');
  content = content.replace(/"AI /g, '"');
  content = content.replace(/'AI /g, "'");
  content = content.replace(/ AI /g, ' ');
  
  // Specific replacements from grep:
  content = content.replace(/AI Strategy Optimizer/g, 'Strategy Optimizer');
  content = content.replace(/AI BMI & GOAL DETERMINATION ENGINE/g, 'BMI & GOAL DETERMINATION ENGINE');
  content = content.replace(/AI Anatomical Determination Engine/g, 'Anatomical Determination Engine');
  content = content.replace(/For AI Calibration/g, 'For Calibration');
  content = content.replace(/AI NUTRITION & MACRO ENGINE/g, 'NUTRITION & MACRO ENGINE');
  content = content.replace(/AI Dietary Guidance/g, 'Dietary Guidance');
  content = content.replace(/Your AI digital gym assistant/g, 'Your digital gym assistant');
  content = content.replace(/Live AI Calibration/g, 'Live Calibration');
  content = content.replace(/AI Calorie & Macro Coach/g, 'Calorie & Macro Coach');
  content = content.replace(/AI 1-Rep Max/g, '1-Rep Max');
  content = content.replace(/AI Calibrated Training Load/g, 'Calibrated Training Load');
  content = content.replace(/AI Nutrition/g, 'Nutrition');
  content = content.replace(/Quick AI Coaching Tip/g, 'Quick Coaching Tip');
  content = content.replace(/AI Coaching Tip/g, 'Coaching Tip');
  content = content.replace(/REPSYNC AI GYM/g, 'REPSYNC GYM');
  content = content.replace(/AI will generate/g, 'The system will generate');
  content = content.replace(/AI BIO-ANIMATION ACTIVE/g, 'BIO-ANIMATION ACTIVE');
  content = content.replace(/Protected AI Command Center/g, 'Protected Command Center');
  
  // Clean up any double spaces created
  content = content.replace(/  +/g, ' ');

  if (content !== original) {
    fs.writeFileSync(file, content, 'utf8');
    console.log(`Updated ${file}`);
  }
});
