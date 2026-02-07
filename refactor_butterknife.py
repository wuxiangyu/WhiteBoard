
import re
import os

def refactor_file(filepath, init_method_name="initView"):
    with open(filepath, 'r') as f:
        content = f.read()

    # 1. Collect BindView fields
    # Pattern: @BindView(R.id.xyz) [mod] Type name;
    # Matches: @BindView(R.id.iv_wb) ImageView mIvWb;
    # Matches: @BindView(R.id.iv_wb)
    #          ImageView mIvWb;
    
    # Simple regex for single line:
    # regex = r'@BindView\((R\.id\.\w+)\)\s*(?:public|private|protected)?\s*(\w+)\s+(\w+);'
    
    # We need to handle multi-line and modifiers.
    # We will iterate lines to be safer or use robust regex.
    
    lines = content.split('\n')
    new_lines = []
    assignments = []
    
    validation_regex = re.compile(r'@BindView\((R\.id\.\w+)\)')
    field_regex = re.compile(r'(?:public|private|protected)?\s*(\w+)\s+(\w+);')
    
    i = 0
    while i < len(lines):
        line = lines[i]
        match = validation_regex.search(line)
        if match:
            r_id = match.group(1)
            # Check next line if current line ends with ) or matches completely
            # Usually @BindView is on one line or above the field.
            
            # If line contains ';', it has the field too.
            if ';' in line:
                # Remove annotation from line
                cleaned_line = validation_regex.sub('', line).strip()
                # Extract field name
                # Assuming standard format "Type name;"
                f_match = field_regex.search(cleaned_line)
                if f_match:
                    field_name = f_match.group(2)
                    assignments.append(f"{field_name} = findViewById({r_id});")
                    new_lines.append(cleaned_line)
                else:
                    # Fallback: keep line but commented or just remove annotation
                    new_lines.append(cleaned_line) # Try to keep field declaration
            else:
                # Annotation is on its own line. Skip it.
                # Next line should be the field.
                i += 1
                if i < len(lines):
                    field_line = lines[i]
                    f_match = field_regex.search(field_line)
                    if f_match:
                        # Find matching field name. 
                        # Be careful about multiple modifiers.
                        # We just want the last word before ';'
                        parts = field_line.strip().split(';')
                        if parts:
                            decl = parts[0].strip().split()
                            if decl:
                                field_name = decl[-1]
                                assignments.append(f"{field_name} = findViewById({r_id});")
                    new_lines.append(field_line)
        else:
            new_lines.append(line)
        i += 1
        
    # 2. Inject assignments
    # Find method definition init_method_name
    # Append assignments at the beginning of it or after super calls.
    
    result_content = '\n'.join(new_lines)
    
    # Injection: look for "void initView() {" or similar
    # In MainActivity it might be onCreate.
    
    insert_point_regex = re.compile(r'(void\s+' + init_method_name + r'\s*\([^)]*\)\s*\{)')
    match = insert_point_regex.search(result_content)
    
    if match:
        end_idx = match.end()
        injection = "\n        ".join(assignments)
        result_content = result_content[:end_idx] + "\n        " + injection + result_content[end_idx:]
    else:
        # Retry with onCreate if initView not found (for MainActivity)
        if init_method_name == "initView":
             # fallback to after setContentView in onCreate
             set_content_view_regex = re.compile(r'(setContentView\(R\.layout\.\w+\);)')
             match_cv = set_content_view_regex.search(result_content)
             if match_cv:
                 end_idx = match_cv.end()
                 injection = "\n        ".join(assignments)
                 result_content = result_content[:end_idx] + "\n        " + injection + result_content[end_idx:]
    
    # 3. Import replacement
    result_content = result_content.replace('import butterknife.BindView;', '')
    result_content = result_content.replace('import butterknife.ButterKnife;', '')
    result_content = result_content.replace('ButterKnife.bind(this);', '')
    
    with open(filepath, 'w') as f:
        f.write(result_content)
    print(f"Processed {filepath}")

# Run for known files
refactor_file('/Users/wuxiangyu/wuxiangyu/code/githubWhiteBoard/WhiteBoard/app/src/main/java/com/example/gpy/whiteboard/MainActivity.java', 'onCreate')
refactor_file('/Users/wuxiangyu/wuxiangyu/code/githubWhiteBoard/WhiteBoard/app/src/main/java/com/example/gpy/whiteboard/view/WhiteBoardActivity.java', 'initView')
