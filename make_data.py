import os
import sys
import argparse
from tqdm import tqdm


def main(input_data_path):
    comp='bazel build -c opt --copt -DMESA_EGL_NO_X11_HEADERS  mediapipe/examples/desktop/multi_hand_tracking:multi_hand_tracking_gpu'
    #comp='bazel build -c opt --define MEDIAPIPE_DISABLE_GPU=1 mediapipe/examples/desktop/multi_hand_tracking:multi_hand_tracking_cpu'
    cmd='GLOG_logtostderr=1 bazel-bin/mediapipe/examples/desktop/multi_hand_tracking/multi_hand_tracking_gpu --calculator_graph_config_file=mediapipe/graphs/hand_tracking/multi_hand_tracking_mobile.pbtxt'
    #cmd='GLOG_logtostderr=1 bazel-bin/mediapipe/examples/desktop/multi_hand_tracking/multi_hand_tracking_gpu --calculator_graph_config_file=mediapipe/graphs/hand_tracking/multi_hand_tracking_desktop_live.pbtxt'
    if os.system(comp) == 0:
        print("Built success !")
    else:
        print("Built failed !")
        exit()

    all_input_data_path = []
    all_output_data_path = []
    all_pose_data_path = []

    output_data_path = input_data_path.replace('input','output')
    pose_data_path = input_data_path.replace('input', 'pose')

    subjects = os.listdir(input_data_path)
    for subject in subjects:
        os.makedirs(output_data_path+'/'+subject, exist_ok=True)
        os.makedirs(pose_data_path+'/'+subject, exist_ok=True)
        poses = os.listdir(input_data_path+'/'+subject)
        for pose in poses:
            os.makedirs(output_data_path+'/'+subject+'/'+pose, exist_ok=True)
            os.makedirs(pose_data_path+'/'+subject+'/'+pose, exist_ok=True)
            videos = os.listdir(input_data_path+'/'+subject+'/'+pose)
            for video in videos:
                #print(os.path.abspath(input_data_path+'/'+subject+'/'+pose+'/'+video))
                all_input_data_path.append(os.path.abspath(input_data_path+'/'+subject+'/'+pose+'/'+video))
                all_output_data_path.append(os.path.abspath(output_data_path+'/'+subject+'/'+pose+'/'+video))
                all_pose_data_path.append(os.path.abspath(pose_data_path+'/'+subject+'/'+pose+'/'+video.replace('.mp4','.txt')))
                if os.path.isfile(os.path.abspath(pose_data_path+'/'+subject+'/'+pose+'/'+video.replace('.mp4','.txt'))):
                    os.remove(os.path.abspath(pose_data_path+'/'+subject+'/'+pose+'/'+video.replace('.mp4','.txt')))


    for i,path in enumerate(tqdm(all_input_data_path)):
        arg1 = ' --input_video_path='+path
        arg2 = ' --output_video_path='+all_output_data_path[i]
        arg3 = ' --output_pose_path='+all_pose_data_path[i]
        cmdret = cmd+arg1+arg2+arg3
        os.system(cmdret)
        print(cmdret)



if __name__ == "__main__":
    parser = argparse.ArgumentParser(description='operating Mediapipe')
    parser.add_argument("--input_data_path",help=" --input data path")
    args=parser.parse_args()
    input_data_path=args.input_data_path
    main(input_data_path)
